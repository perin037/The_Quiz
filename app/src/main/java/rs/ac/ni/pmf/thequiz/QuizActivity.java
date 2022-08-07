package rs.ac.ni.pmf.thequiz;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import rs.ac.ni.pmf.thequiz.model.Question;
import rs.ac.ni.pmf.thequiz.model.QuestionsRepository;

public class QuizActivity extends AppCompatActivity {

        public static final String TAG = "MY_QUIZ";

        public static final String KEY_USERNAME = "USERNAME";
        public static final String KEY_SCORE = "SCORE";

        private static final String KEY_CURRENT_SCORE = "CURRENT_SCORE";

        private static final long BACK_BUTTON_ACCEPT_DELAY = 2000;
        private static final long COUNT_DOWN_INTERVAL = 15000;

        private List<Question> _questions;

        private int _currentQuestionIndex;
        private int _score;
        private boolean _answered;
        private long _millisLeft;

        public static final ActivityResultContract<String, Integer> QUIZ_CONTRACT = new ActivityResultContract<String, Integer>() {
            @NonNull
            @Override
            public Intent createIntent(@NonNull Context context, String input) {
                final Intent intent = new Intent(context, QuizActivity.class);
                intent.putExtra(KEY_USERNAME, input);
                return intent;
            }

            @Override
            public Integer parseResult(int resultCode, @Nullable Intent intent) {
                if (intent == null) {
                    return -1;
                }

                return intent.getIntExtra(KEY_SCORE, 0);
            }
        };

        private TextView _textScore;
        private TextView _textQuestionCount;
        private TextView _textCountDown;

        private TextView _questionText;
        private RadioGroup _radioGroup;
        private RadioButton _radioButton1;
        private RadioButton _radioButton2;
        private RadioButton _radioButton3;

        private Button _confirmButton;

        private Question _currentQuestion;

        private ColorStateList _defaultRadioButtonColor;
        private ColorStateList _defaultCounterTextColor;

        private long _backButtonClickedMillis = 0;

        private CountDownTimer _countDownTimer;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_quiz);

            _answered = false;

            _confirmButton = findViewById(R.id.button_confirm_next);
            _confirmButton.setOnClickListener(view -> {
                if (_answered) {
                    showNextQuestion();
                } else {
                    if (_radioButton1.isChecked() || _radioButton2.isChecked() || _radioButton3.isChecked()) {
                        checkAnswer();
                    } else {
                        Toast.makeText(QuizActivity.this, R.string.select_answer_warning, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            _textScore = findViewById(R.id.text_view_score);
            _textQuestionCount = findViewById(R.id.text_view_question_count);
            _textCountDown = findViewById(R.id.text_view_countdown);

            _questionText = findViewById(R.id.text_view_question);
            _radioGroup = findViewById(R.id.radio_group);
            _radioButton1 = findViewById(R.id.radio_button1);
            _radioButton2 = findViewById(R.id.radio_button2);
            _radioButton3 = findViewById(R.id.radio_button3);

            _defaultRadioButtonColor = _radioButton1.getTextColors();
            _defaultCounterTextColor = _textCountDown.getTextColors();

            _questions = QuestionsRepository.findAll();
            _currentQuestionIndex = -1;

            _millisLeft = COUNT_DOWN_INTERVAL;

            if (savedInstanceState == null) {
                _score = 0;
            } else {
                _score = savedInstanceState.getInt(KEY_CURRENT_SCORE, 0);
            }

            updateScore();

            showNextQuestion();
        }

        private void updateScore() {
            _textScore.setText(getResources().getString(R.string.score_display, _score));
        }

        private void checkAnswer() {
            _answered = true;
            _countDownTimer.cancel();

            final int userAnswer = _radioGroup.indexOfChild(findViewById(_radioGroup.getCheckedRadioButtonId())) + 1;

            if (userAnswer == _currentQuestion.getAnswer())
            {
                _score++;
                updateScore();
            }

            _radioButton1.setTextColor(Color.RED);
            _radioButton2.setTextColor(Color.RED);
            _radioButton3.setTextColor(Color.RED);

            switch (_currentQuestion.getAnswer()) {
                case 1:
                    _radioButton1.setTextColor(Color.GREEN);
                    break;
                case 2:
                    _radioButton2.setTextColor(Color.GREEN);
                    break;
                case 3:
                    _radioButton3.setTextColor(Color.GREEN);
                    break;
            }

            if (_currentQuestionIndex < _questions.size() - 1) {
                _confirmButton.setText(R.string.next_question);
            } else {
                _confirmButton.setText(R.string.finish_quiz);
            }
        }

        private void showNextQuestion() {
            _answered = false;
            _currentQuestionIndex++;
            int totalQuestions = _questions.size();

            _radioGroup.clearCheck();
            _radioButton1.setTextColor(_defaultRadioButtonColor);
            _radioButton2.setTextColor(_defaultRadioButtonColor);
            _radioButton3.setTextColor(_defaultRadioButtonColor);

            _confirmButton.setText(R.string.confirm_answer);

            if (_currentQuestionIndex < totalQuestions) {
                _currentQuestion = _questions.get(_currentQuestionIndex);
                _textQuestionCount.setText(getResources().getString(R.string.answered_questions_display, _currentQuestionIndex + 1, totalQuestions));

                _questionText.setText(_currentQuestion.getQuestion());
                _radioButton1.setText(_currentQuestion.getOption1());
                _radioButton2.setText(_currentQuestion.getOption2());
                _radioButton3.setText(_currentQuestion.getOption3());

                startCountDown();
            } else {
                finishQuiz();
            }
        }

        private void startCountDown() {
            _countDownTimer = new CountDownTimer(_millisLeft, 1000) {
                @Override
                public void onTick(long millisLeft) {
                    _millisLeft = millisLeft;
                    updateCountDown(millisLeft);
                }

                @Override
                public void onFinish() {
                    _millisLeft = COUNT_DOWN_INTERVAL;
                    checkAnswer();
                }
            };
            _countDownTimer.start();
        }

        private void updateCountDown(long millisLeft) {
            int minutes = (int)(millisLeft / 1000) / 60;
            int seconds = (int)(millisLeft / 1000) % 60;

            _textCountDown.setText(String.format(Locale.ROOT, "%02d:%02d", minutes, seconds));

            if (millisLeft < 10000) {
                _textCountDown.setTextColor(Color.RED);
            } else {
                _textCountDown.setTextColor(_defaultCounterTextColor);
            }
        }

        private void finishQuiz() {
            final Intent intent = new Intent();
            intent.putExtra(KEY_SCORE, _score);
            setResult(RESULT_OK, intent);
            finish();
        }

        @Override
        public void onBackPressed() {
            if (System.currentTimeMillis() - _backButtonClickedMillis < BACK_BUTTON_ACCEPT_DELAY)
            {
                finishQuiz();
            } else {
                Toast.makeText(this, R.string.back_pressed_warning, Toast.LENGTH_SHORT).show();
            }

            _backButtonClickedMillis = System.currentTimeMillis();
        }

        @Override
        protected void onSaveInstanceState(@NonNull Bundle outState) {
            super.onSaveInstanceState(outState);

            outState.putInt(KEY_CURRENT_SCORE, _score);
        }
    }