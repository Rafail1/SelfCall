package com.hfad.selfcall.Classes;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.widget.Toast;

import com.hfad.selfcall.AuthActivity;
import com.hfad.selfcall.Net.PostStartCall;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

public class CallReceiver extends PhonecallReceiver {
    static MediaRecorder recorder;
    private AudioManager audioManager;

    private MediaRecorder getRecorder(boolean lineWays) {
        recorder = new MediaRecorder();
        recorder.reset();
        if(!lineWays) {
            recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
        } else {
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        }

        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        return recorder;
    }

    @Override
    protected void onSpeakStarted(Context ctx, String number, Date start, boolean... l) throws IOException {
        if(AuthActivity.t1 != null) {
            AuthActivity.t1.stopToast.set(true);
        }
        if(audioManager == null) {
            audioManager = (AudioManager)ctx.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.MODE_IN_CALL);
        }
        boolean line = false;
        if(l.length > 0) {
            line = l[0];
        }
        if(recorder == null) {
            recorder = getRecorder(line);
        } else {
            recorder.stop();
        }



        File folder = new File(Environment.getExternalStorageDirectory() + "/myApp");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        String of = null;
        if (success) {
            of = folder.getAbsolutePath();
        } else {
            return;
        }

        recorder.setOutputFile(of+"/"+start.getTime()+".mp4");
        try {
            recorder.prepare();
            recorder.start();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_LONG);
            toast.show();
            recorder = null;
            onSpeakStarted(ctx, number, start,!line);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_LONG);
            toast.show();
            recorder = null;
            onSpeakStarted(ctx, number, start, !line);
            return;
        }
    }

    private void sendStatus(int status, String number){
        ContentValues cv = new ContentValues();
        cv.put("number", number);
        cv.put("status", status);
        new PostStartCall(cv).execute();
    }

    private void onStartCall(final Context context, String number, Date start) throws IOException {
        // отсылать номер, на который звонят
        if(AuthActivity.mch == null) {
            return;
        }



        int id = AuthActivity.mch.getContactIdByNumber(number);
        if(id >= 0) {
            String description = AuthActivity.users.getById(id);
            if(description != null) {
                Toast toast = Toast.makeText(context, description, Toast.LENGTH_LONG);
                toast.show();
            }
        } else {
            String desc[] = {
                    "— Она такая красивая … прямо как роза! А когда ноги не побреет, так вообще сходство поразительное …",
                    "Самые новые батарейки «Дети Прокурора»!\n" +
                            "          — Батарейки «Дети Прокурора» — не сядут никогда.",
                    "Кто не курит и не пьет, тот на органы пойдет.",
                    "Жизнь говно...Но я с лопатой!",
                    "Золотое правило: у кого золото, у того и правила.",
                    "Отказался от счастья... узнав, что оно не в деньгах.",
                    "Раз, два, три - больше бегай, меньше жри!",
                    "Интуиция — это способность головы чуять жопой...",
                    "Вон, смотри, звезда падает! Загадай желание.\n" +
                            "— Я хочу, чтобы ты на мне женился.\n" +
                            "— Ой, смотри, обратно полетела...",
                    "к чёрту лeтo. я хoчу, чтoбы былo тeмнo, и тумaннo, и хoлoднo и вooбщe oктябрь",
                    "– Почему Яндекс так долго ищет? – Он гуглит.",
                    "Мастер спорта по пинанию детородного органа.",
                    "А спонсор этого дня — работа за еду. \n" +
                            "Работа за еду — вместо тысячи - плов",
                    "- Привет! Давно не виделись, как ты возмужал! \n" +
                            "- Привет! Да ты тоже возбабела...",
            };


            Random random = new Random();
            int i = random.nextInt(desc.length);
            final Toast toast = Toast.makeText(context, desc[i], Toast.LENGTH_SHORT);

            AuthActivity.t1 = new ToastShower(toast);
            AuthActivity.t1.start();
        }
        AuthActivity.mch.setLastNumber(number);
    }

    private void onStopCall(Context ctx, String number, Date start, Date end, boolean out) throws IOException {
        sendStatus(0, number);
        if(AuthActivity.t1 != null) {
            AuthActivity.t1.stopToast.set(true);
        }
        if(audioManager == null) {
            audioManager = (AudioManager)ctx.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.MODE_IN_CALL);
        }
        try{
            if (null != recorder) {
                recorder.stop();
                recorder.reset();
                recorder.release();
                recorder = null;
            } else {
                return;
            }
        } catch(RuntimeException stopException) {
            Toast toast = Toast.makeText(ctx, stopException.getMessage(), Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        File folder = new File(Environment.getExternalStorageDirectory() + "/myApp");
        String of = folder.getAbsolutePath()+"/"+start.getTime()+".mp4";


        Calls calls = new Calls(AuthActivity.mdh.getWritableDatabase());
        long dur = end.getTime() - start.getTime();

        int inOut = out ? 1 : 0;

        calls.addCall(start, dur, number, inOut, of);
        if(AuthActivity.mch.getContactIdByNumber(number) == -1) {
            Intent intent = new Intent(ctx, AddContact.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
        }
    }


    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        try {
            sendStatus(1, number);
            onStartCall(ctx, number, start);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        try {

            sendStatus(2, number);
            onStartCall(ctx, number, start);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        try {
            onStopCall(ctx, number, start, end, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        try {
            onStopCall(ctx, number, start, end, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        try {
            onStopCall(ctx, number, start, start, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}