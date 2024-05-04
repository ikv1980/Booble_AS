package com.example.booble;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public String number = "", result = "", example = "", answer = "", error = "";
    Boolean flag = false;
    public Float per = (float) 0;
    public TextView textView;
    public EditText editText;
    public Button button0, button1, button2, button3, button4, button5, button6, button7, button8, button9, clear, backspace, percent, div, multi, minus, add, sign, dot, equal;
    String[] messages = {
            "eСила – не в бабках. Ведь бабки – уже старые",
            "eВзял член - дрочи!",
            "eВзял нож - режь, взял дошик - ешь",
            "eЯ живу, как карта ляжет. Ты живёшь, как мамка скажет",
            "eНикогда не сдавайтесь, идите к своей цели! А если будет сложно – сдавайтесь",
            "eЕсли заблудился в лесу, иди домой",
            "eЗапомни: всего одна ошибка – и ты ошибся",
            "eВ жизни всегда есть две дороги: одна — первая, а другая — вторая",
            "eМы должны оставаться мыми, а они – оними",
            "eДелай, как надо. Как не надо, не делай",
            "eСниму квартиру. Порядок на районе гарантирую",
            "eРабота — это не волк. Работа — ворк. А волк — это ходить",
            "eНе будьте эгоистами, в первую очередь думайте о себе!",
            "eКак говорил мой дед, «Я твой дед»",
            "eСлово - не воробей. Вообще ничто не воробей, кроме самого воробья",
            "eЕсли тебе где-то не рады в рваных носках, то и в целых туда идти не стоит",
            "eЧак Норрис может убить два камня одной птицей",
            "eЧак Норрис не здоров как бык, это бык здоров как Чак Норрис",
            "eЧак Норрис может захлопнуть вращающуюся дверь",
            "eЯ однажды задушил бандита беспроводным телефоном",
            "eЧак Норрис может выжать апельсиновый сок из лимона",
            "eЧак Норрис вызывает у сигарет рак",
            "eКогда Чак Норрис бросает бумеранг, тот не возвращается",
            "eЧак Норрис может довести до оргазма резиновую женщину",
            "eКогда Чак Норрис тренируется, тренажёр становится сильнее",
            "eЧак Норрис может слепить снеговика из дождя",
            "eЧак Норрис никогда не спит. Он выжидает",
            "eКогда Чак Норрис родился, он сам вынес свою маму из роддома",
            "eЧак Норрис может заставить плакать лук",
            "eЧак Норрис выиграет у вас в крестики-нолики за один ход",
            "eЧак Норрис может дышать даже вакуумом",
            "eКупилл ей крем для ухода, а она не уходит",
            "eВ казахском плове может быть до трех лошадиных сил",
            "eВ начале комплименты, потом платишь алименты",
            "eЖена единственная вредная привычка, которая может бросить тебя сама",
            "eСколько человеку лет, столько ему и зим",
            "eЛучше какнуть и пукнуть, чем пукнуть и какнуть",
            "eЛучше 5 см спереди, чем 25 сзади",
            "eЛучше посрать и опоздать, чем успеть и обосраться",
            "eСражаться с гномами, это очень низко",
            "eАлкоголь убивает нервные клетки, остаются только спокойные",
            "eЕсли пьянка неизбежна - пей первый",
            "eТы не ты, когда не ты",
            "eЗавтра вставать рано - встану послезавтра",
            "eЗапомни! А то забудешь",
            "eМой член был в книге рекордов Гиннеса, пока не выгнали из библиотеки",
            "eНе доверяй никому, даже никому",
            "eЕсли есть число пи, значит есть число дор",
            "eОни стояли втроем: она, он и у него"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        init();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void init(){
        textView = findViewById(R.id.textView);
        editText = findViewById(R.id.editText);

        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        button6 = findViewById(R.id.button6);
        button7 = findViewById(R.id.button7);
        button8 = findViewById(R.id.button8);
        button9 = findViewById(R.id.button9);

        clear = findViewById(R.id.clear);
        backspace = findViewById(R.id.backspace);
        percent = findViewById(R.id.percent);
        div = findViewById(R.id.div);
        multi = findViewById(R.id.multi);

        minus = findViewById(R.id.minus);
        add = findViewById(R.id.add);
        sign = findViewById(R.id.sign);
        dot = findViewById(R.id.dot);
        equal = findViewById(R.id.equal);
    }
    public void BtnClick(View view) {
        int id = view.getId();
        // Обнуление поля ввода, если стоит "0"
        if (number.equals("-0")) {
            number = "-";
        }
        // Очистка поля ввода
        if (id == R.id.clear) {
            cleanAll();
        }
        // Удаление последнего символа
        if (id == R.id.backspace) {
            if (!number.isEmpty()) {
                number = number.substring(0, number.length() - 1);
            }
            if(result.endsWith("0/") || result.endsWith("0*") || result.endsWith("0-") || result.endsWith("0+")){
                result = "";
                example = "";
            }
        }
        // Ввод чисел
        if (id == R.id.button0 && !Objects.equals(number, "0")) {
            number += "0";
        } else if (id == R.id.button1) {
            number += "1";
        } else if (id == R.id.button2) {
            number += "2";
        } else if (id == R.id.button3) {
            number += "3";
        } else if (id == R.id.button4) {
            number += "4";
        } else if (id == R.id.button5) {
            number += "5";
        } else if (id == R.id.button6) {
            number += "6";
        } else if (id == R.id.button7) {
            number += "7";
        } else if (id == R.id.button8) {
            number += "8";
        } else if (id == R.id.button9) {
            number += "9";
        }
        // знак точки
        if (id == R.id.dot && number.indexOf('.') == -1) {
            number = number.isEmpty()?"0.":number+".";
        }
        // знак минуса
        if (id == R.id.sign) {
            number = (number.isEmpty()  || number.equals("0"))? "0" : number;
            number = (number.charAt(0) == '-') ? number.substring(1) : ("-" + number);
        }
        // Арифметические операции
        if (id == R.id.div) {
            Operation("/");
        } else if (id == R.id.multi) {
            Operation("*");
        } else if (id == R.id.minus) {
            Operation("-");
        } else if (id == R.id.add) {
            Operation("+");
        }
        // знак процента
        if (id == R.id.percent && !number.isEmpty()) {
            per = Float.parseFloat(number)/100;
            number = String.valueOf(per);
        }
        // знак равенства
        if (id == R.id.equal) {
            flag = true;
            textView.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_NONE);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 45);
            editText.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_NONE);
            editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60);
            textView.setTextColor(Color.parseColor("#000000"));
            editText.setTextColor(Color.parseColor("#3C3C3C"));
        }

        // Вывод данных на экран
        OutputScreen();
//        Log.d("MyApp", number + ": " + number.getClass().getName());
        if (number.equals("25051980")){
            textView.setText("Ну и чего ты хочешь, целовальник кукольных поп?");
            textView.setTextColor(Color.parseColor("#FF0000"));
        }
    }

    @SuppressLint("SetTextI18n")
    private void OutputScreen() {
        // Если в number мусор, то чистим его
        if (number.isEmpty() || Objects.equals(number, "-") || Objects.equals(number, ".") || Objects.equals(number, "-.")) {
            number = "";
        }

        String answ = result + number;
        if(!(answ).isEmpty()){
            if(answ.endsWith("/") || answ.endsWith("*") || answ.endsWith("-") || answ.endsWith("+")){
                answ = answ.substring(0, answ.length() - 1);
            }
            answer = Calculation(answ);
            if(answer.charAt(0) == 'e'){
                // Выводим ошибки
                error = answer.substring(1);
                cleanAll();
                textView.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_NONE);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                textView.setTextColor(Color.parseColor("#FF0000"));
                textView.setText(error);
                error = "";
                return;
            } else {
                answer = "=" + NumberToString(answer);
                if(flag.equals(true)){
                    example = "";
                    result = "";
                    number = answer.substring(1);
                }
            }
        }

        editText.setText(answer.isEmpty()?"0":answer);
        if(example.isEmpty()){
            textView.setText(number.isEmpty()?"0":number);
        } else {
            textView.setText(example + (number.isEmpty() ?"":number));
        }
        flag = false;
    }
    private void cleanAll() {
        number = "";
        result = "";
        example = "";
        answer = "";
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 45);
        editText.setAutoSizeTextTypeUniformWithConfiguration(10, 45, 1, TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        textView.setAutoSizeTextTypeUniformWithConfiguration(10, 60, 1, TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        textView.setTextColor(Color.parseColor("#3C3C3C"));
        editText.setTextColor(Color.parseColor("#000000"));
    }
    public void Operation(String id){
        if(result.isEmpty()){
            number = (number.isEmpty() || number.equals("-0") || number.equals("-"))?"0":number;
        }
        if(number.isEmpty()){
            result = result.substring(0, result.length() - 1);
            example = example.substring(0, example.length() - 1);
            result += id;
            example += Operand(id);
        } else {
            result = result + number + id;
            example = example + number + Operand(id);
        }
        number = "";
    }
    public String Calculation(String id){
        Log.w("MyApp", id + ": " + id.getClass().getName());
        try {
            if (id.contains("/0")) {
                return message();
            }
            Expression expression = new ExpressionBuilder(id).build();
            double result = expression.evaluate();
            return String.valueOf(result);
        } catch (Exception e) {
            return "eЧёт не срослось, увы.";
        }
    }
    public String NumberToString(String value){
        String result;
        if (value.endsWith(".0")) {
            result = value.substring(0, value.length() - 2);
            return result;
        } else {
            DecimalFormat df = new DecimalFormat("#.####");
            double parsedNumber = Double.parseDouble(value);
            value = df.format(parsedNumber);
            return value;
        }
    }
    public String Operand(String id){
        switch (id){
            case "/":
                return "÷";
            case "*":
                return "×";
            case "-":
                return "-";
            default:
                return "+";
        }
    }
    public String message() {
        Random random = new Random();
        int randomIndex = random.nextInt(messages.length);
        return messages[randomIndex];
    }
}