package rs.ac.ni.pmf.thequiz;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class StartScreenActivity extends AppCompatActivity {

    private ActivityResultLauncher<String> _quizActivityLauncher;

    private String _username = "USER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        _quizActivityLauncher = registerForActivityResult(QuizActivity.QUIZ_CONTRACT, this::onQuizFinished);

        final Button startQuizButton = findViewById(R.id.button_start_quiz);
        startQuizButton.setOnClickListener(this::startQuiz);
    }

    private void startQuiz(View view) {
        _quizActivityLauncher.launch(_username);
    }

    private void onQuizFinished(final Integer result)
    {
        Log.i("QUIZ", "Result: " + result);
    }
}