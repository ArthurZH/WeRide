package com.example.deanwen.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class SplitBillActivity extends ActionBarActivity {
    private static final String username = "chanliu524@gmail.com";
    private static final String password = "a1234567!";

    private ListView listView;
    private MyAdapter2 adapter;
    private ArrayList<FriendItem> list = new ArrayList<FriendItem>();
    public static String total_money = "Total:$0";
    public static String name = "";
    public static String email = "";

    public static void start(Context context) {
        Intent intent = new Intent(context, SplitBillActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_main);
        TextView textView = (TextView) findViewById(R.id.label);
        textView.setText(total_money);
        System.out.println("Total money: " + total_money);
        Button sendButton = (Button) findViewById(R.id.send_email);

        List<Record> records = FBActivity.db.getAllRecords();
        for(Record r : records) {
            FriendItem f = new FriendItem(r.get_name(), r.get_email());
            list.add(f);
        }

        listView = (ListView) findViewById(R.id.listView2);
        this.adapter = new MyAdapter2(list, this);
        listView.setAdapter(this.adapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String subject = "Bill Payment -- WeRide Application";

                List<Record> records = FBActivity.db.getAllRecords();
                long total_seconds = 0;
                for(Record r : records) {
                    long start = Long.parseLong(r.get_start_time());
                    long end = Long.parseLong(r.get_end_time());
                    total_seconds += end - start;
                }
                System.out.println("----------------" + total_money);
                double total = Double.parseDouble(total_money.substring(total_money.indexOf("$") + 1));
                System.out.println("Total money++++++++++:" + total);
                for(Record r : records) {
                    System.out.println("****************" + r.get_name());
                    if(r.get_email().equals(SplitBillActivity.email)) {
                        continue;
                    }
                    long start = Long.parseLong(r.get_start_time());
                    long end = Long.parseLong(r.get_end_time());
                    double cur_money = (end - start) * 1.0 / total_seconds * total;
                    String message = "Please send " + name + "(" + email + ") $" + String.format(Locale.US, "%.2f", cur_money) + "--WeRide Application";
                    sendMail(r.get_email(), subject, message);
                }

                FBActivity.db.deleteTable();
            }
        });

        Button endButton = (Button) findViewById(R.id.end);
        endButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d("Read: ", "Reading all records...");
                List<Record> records = FBActivity.db.getAllRecords();
                for(Record r : records) {
                    String log = "Email: " + r.get_email()
                            + " ,Name: " + r.get_name()
                            + " ,Start_time: " + r.get_start_time()
                            + " ,End_time: " + r.get_end_time();
                    Log.d("Record: ", log);
                }
                startActivity(new Intent(SplitBillActivity.this, UberMainActivity.class));
            }
        });

        final Button startButton = (Button) findViewById(R.id.start);
        startButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                List<Record> records = FBActivity.db.getAllRecords();
                long time = System.currentTimeMillis();
                for(Record r : records) {
                    r.set_start_time(String.valueOf(time));
                    FBActivity.db.updateRecord(r);
                }
                startButton.setEnabled(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_split_bill, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendMail(String email, String subject, String messageBody) {
        Session session = createSessionObject();

        try {
            Message message = createMessage(email, subject, messageBody, session);
            new SendMailTask().execute(message);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private Message createMessage(String email, String subject, String messageBody, Session session) throws MessagingException, UnsupportedEncodingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("chanliu524@gmail.com", "WeRide Application"));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email, email));
        message.setSubject(subject);
        message.setText(messageBody);
        return message;
    }

    private Session createSessionObject() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        return Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    private class SendMailTask extends AsyncTask<Message, Void, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(SplitBillActivity.this, "Please wait", "Sending mail", true, false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
