package edu.uw.ischool.jacoblu.awty

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.app.ActivityCompat
import android.telephony.SmsManager
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(this, arrayOf (Manifest.permission.SEND_SMS),1);
        val messageInput:EditText = findViewById(R.id.Message) as EditText
        val phoneInput:EditText = findViewById<EditText>(R.id.Phone)
        val timeintervalInput:EditText = findViewById<EditText>(R.id.TimeInterval)
        val startOrStopButton:Button = findViewById<Button>(R.id.StartOrStop)
        val alarm:AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this,AlarmReceiver::class.java)

        startOrStopButton.setOnClickListener {
            when (startOrStopButton.text) {
               "Start" -> {
                    if (messageInput.text.isNotEmpty() && phoneInput.text.toString().length != 0 && timeintervalInput.text.toString().toLong() > 0) {
                        startOrStopButton.text = "Stop"
                        intent.putExtra("MessageInput", messageInput.text.toString())
                        intent.putExtra("NumberInput", phoneInput.text.toString())

                        val alarmIntent = PendingIntent.getBroadcast(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                        alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),
                                1000 * 60 * timeintervalInput.text.toString().toLong(), alarmIntent)
                    } else {
                        if (!messageInput.text.isNotEmpty()) {
                            Toast.makeText(this, "Text box cannot be empty", Toast.LENGTH_LONG).show()
                        } else if (phoneInput.text.toString().length == 0) {
                            Toast.makeText(this, "Phone number must have some digits", Toast.LENGTH_LONG).show()
                        } else if (timeintervalInput.text.toString().toLong() <= 0) {
                            Toast.makeText(this, "Interval must be larger than 0 minutes", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                "Stop" -> {
                    startOrStopButton.text = "Start"
                    val alarmIntent = PendingIntent.getBroadcast(applicationContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
                    alarm.cancel(alarmIntent)
                }
            }
        }
    }
}

class AlarmReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        val number = intent.getStringExtra("NumberInput")
        val message = intent.getStringExtra("MessageInput")
//        Toast.makeText(context, "(" + number.substring(0..2) + ") " +
//                number.substring(3..5) + "-" + number.substring(6)
//                + ": " + message, Toast.LENGTH_LONG).show()
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(number, null, message, null, null)
        Log.i("mainactivity","works!")
    }
}
