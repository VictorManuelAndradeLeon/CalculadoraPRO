package com.example.calculadora;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvRes = findViewById(R.id.tvRes);
    }

    public void calcular(View view) {
        Button boton = (Button) view;
        String textoBoton = boton.getText().toString();
        String concatenar = tvRes.getText().toString() + textoBoton;
        String concatenarSinCeros = quitarCerosIzquirda(concatenar);
        if (textoBoton.equals("=")) {
            double resultado = 0.0;
            try {
                resultado = eval(tvRes.getText().toString());
                tvRes.setText(Double.toString(resultado));
            } catch (Exception e) {
                tvRes.setText(e.toString());
            }
        } else if (textoBoton.equals("AC")) {
            tvRes.setText("0");
        }else if (textoBoton.equals("←")) {
            tvRes.setText(tvRes.getText().toString().substring(0, tvRes.getText().length() - 1));
        } else {
            tvRes.setText(concatenarSinCeros);
        }
    }

    public String quitarCerosIzquirda(String str) {
        int i = 0;
        while (i < str.length() && str.charAt(i) == '0') {
            i++;
        }
        StringBuffer sb = new StringBuffer(str);
        sb.replace(0, i, "");
        return sb.toString();
    }

    public double eval(String str) {
        return new Object() {
            int pos = -1;
            int ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') {
                    nextChar();
                }
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                while (true) {
                    if (eat('+')) {
                        x += parseTerm(); // addition
                    } else if (eat('-')) {
                        x -= parseTerm(); // subtraction
                    } else {
                        return x;
                    }
                }
            }

            double parseTerm() {
                double x = parseFactor();
                while (true) {
                    if (eat('*')) {
                        x *= parseFactor(); // multiplication
                    } else if (eat('/')) {
                        x /= parseFactor(); // division
                    } else {
                        return x;
                    }
                }
            }

            double parseFactor() {
                if (eat('+')) {
                    return parseFactor(); // unary plus
                }
                if (eat('-')) {
                    return -parseFactor(); // unary minus
                }
                double x;
                int startPos = pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if (ch >= '0' && ch <= '9' || ch == '.') { // numbers
                    while (ch >= '0' && ch <= '9' || ch == '.') {
                        nextChar();
                    }
                    x = Double.parseDouble(str.substring(startPos, pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }
                if (eat('(')) { // handle expressions within parentheses
                    x = parseExpression(); // evaluate expression inside parentheses first
                    eat(')');
                }
                return x;
            }
        }.parse();
    }

}