package com.example.android.quizapp;

import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static android.R.attr.type;
import static android.text.Html.escapeHtml;

public class MainActivity extends AppCompatActivity {

    ArrayList<Spanned> questionsList;
    ArrayList<Spanned> correctAnswerList;
    ArrayList<Integer> correctAnswerIndex;
    ArrayList<Spanned[]> incorrectAnswersList;
    String argArray[];
    String playerName;
    int score;
    final String TAG = "MainActivity";

    LinearLayout questionList;
    Button submitButton;
    View blurBackgroundView;
    Spinner categorySpinner;
    Spinner difficultySpinner;
    HashMap<String, String> difficulties;
    HashMap<String, String> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeCategories();
        initializeDifficulties();
        prepareStartScreen();
    }

    /**
     * Create the HashMap to map categories to api codes
     */
    protected void initializeCategories() {
        categories = new HashMap<>();
        categories.put("Anime", "31");
        categories.put("Art", "25");
        categories.put("Computer Science", "18");
        categories.put("History", "23");
        categories.put("Mathematics", "19");
        categories.put("Politics", "24");
        categories.put("Sports", "21");
    }
    /**
     * Create the HashMap to map difficulties to api codes
     */
    protected void initializeDifficulties() {
        difficulties = new HashMap<>();
        difficulties.put("Easy", "easy");
        difficulties.put("Medium", "medium");
        difficulties.put("Hard", "hard");
    }


    /**
     * Start is pressed, so create a new quiz after retrieving parameters
     *
     * @param v
     */
    protected void startNewGame(View v) {
        /* Retrieve Quiz Parameters */
        playerName = new String( ((EditText) findViewById(R.id.name_edittext)).getText().toString());
        argArray = new String[]{categorySpinner.getSelectedItem().toString(),
                difficultySpinner.getSelectedItem().toString()};
        setContentView(R.layout.activity_main);
        /* Initialize global variables */
        questionList = (LinearLayout) findViewById(R.id.question_list);
        submitButton = (Button) findViewById(R.id.submit_button);
        blurBackgroundView = findViewById(R.id.blur_background);
        /* Create quiz */
        createQuiz();
    }

    /**
     * Setup start screen spinners
     */
    protected void prepareStartScreen() {
        setContentView(R.layout.start_screen);
        categorySpinner = (Spinner) findViewById(R.id.category_spinner);
        difficultySpinner = (Spinner) findViewById(R.id.difficulty_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, R.layout.spinner_style);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        categorySpinner.setAdapter(adapter);


        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = ArrayAdapter.createFromResource(this,
                R.array.difficulty_level_array, R.layout.spinner_style);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        difficultySpinner.setAdapter(adapter);
    }

    /**
     * Create a new quiz
     */
    protected void createQuiz() {
        Log.v(TAG, "Calling GetQuiz to retrieve questions from network");
        questionList.removeAllViews();
        correctAnswerIndex = new ArrayList<>();
        questionsList = new ArrayList<>();
        correctAnswerList = new ArrayList<>();
        incorrectAnswersList = new ArrayList<>();
        submitButton.setVisibility(View.GONE);
        new GetQuiz().execute(argArray);
    }

    /**
     * Check given answers and calculate score
     * @param v
     */
    protected void checkAnswers(View v) {
        /* Check answers and calculate score */
        score = 0;
        for (int questionNumber = 0; questionNumber < questionList.getChildCount(); questionNumber++) {
            GridLayout options = (GridLayout) ((HorizontalScrollView) ((LinearLayout) questionList.getChildAt(questionNumber)).getChildAt(1)).getChildAt(0);
            for (int optNumber = 0; optNumber < options.getChildCount(); optNumber++) {
                RadioButton opt = (RadioButton) options.getChildAt(optNumber);
                if (opt.isChecked()) {
                    opt.setChecked(false);
                    if (optNumber == correctAnswerIndex.get(questionNumber))
                        score++;
                }
            }
        }
        displayScorePopup();
    }


    /**
     * Display popum window showing score and options
     */
    protected void displayScorePopup(){
                /* Create and show popup */
        blurBackgroundView.setVisibility(View.VISIBLE);
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View scoreView = inflater.inflate(R.layout.score_popup, null);
        final PopupWindow mPopupWindow = new PopupWindow(
                scoreView,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        if (Build.VERSION.SDK_INT >= 21) {
            mPopupWindow.setElevation(6.0f);
        }

        /* Setup onClick method for Retry Button */
        Button retryButton = (Button) scoreView.findViewById(R.id.retry_button);
        retryButton.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blurBackgroundView.setVisibility(View.INVISIBLE);
                mPopupWindow.dismiss();
            }
        });

        /* Setup onClick method for New (Quiz) Button */
        Button newQuizButton = (Button) scoreView.findViewById(R.id.new_quiz_button);
        newQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blurBackgroundView.setVisibility(View.INVISIBLE);
                mPopupWindow.dismiss();
                createQuiz();
            }
        });

        /* Setup onClick method for Main Menu Button */
        Button mainMenuButton = (Button) scoreView.findViewById(R.id.main_menu_button);
        mainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blurBackgroundView.setVisibility(View.INVISIBLE);
                mPopupWindow.dismiss();
                prepareStartScreen();

            }
        });

        /* Display Score */
        TextView scoreTextView = (TextView) scoreView.findViewById(R.id.score_textview);
        scoreTextView.setText(getString(R.string.score) + ":  " + score);

        /* Display Player Name */
        TextView nameTextView = (TextView) scoreView.findViewById(R.id.player_name_textview);
        nameTextView.setText(playerName);

        mPopupWindow.showAtLocation((RelativeLayout) findViewById(R.id.activity_main), Gravity.CENTER, 0, 0);
    }

    /**
     * Create a new question with with number questionNumber
     * @param questionNumber
     */
    protected void createQuestion(int questionNumber) {
        /* Inflate layout for a new question */
        LayoutInflater questionInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        questionInflater.inflate(R.layout.question_template, questionList);

        /* Get the TextView containing the text of the question */
        TextView questionText = (TextView) ((LinearLayout) questionList.getChildAt(questionNumber)).getChildAt(0);
        questionText.setText((questionNumber + 1) + ". " + questionsList.get(questionNumber));

        /* Get the GridLayout Viewgroup containing the RadioButtons */
        GridLayout radiobuttonGrid = (GridLayout) ((HorizontalScrollView) ((LinearLayout) questionList.getChildAt(questionNumber)).getChildAt(1)).getChildAt(0);
        int incCount = 0;

        /* Choose a random RadioButton to assign the correct answer */
        Random r = new Random();
        int correctNumber = r.nextInt(3 - 0) + 1;
        correctAnswerIndex.add(correctNumber);

        /* Assign answers to RadioButtons */
        for (int i = 0; i < radiobuttonGrid.getChildCount(); i++) {
            RadioButton radio = (RadioButton) radiobuttonGrid.getChildAt(i);
            if (i == correctNumber)
                radio.setText(correctAnswerList.get(questionNumber));
            else {
                radio.setText(incorrectAnswersList.get(questionNumber)[incCount]);
                incCount++;
            }
            /* Add onClickListener so only 1 RadioButton can be selected at a time */
            radio.setOnClickListener(new RadioButton.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GridLayout p = (GridLayout) v.getParent();
                    for (int j = 0; j < p.getChildCount(); j++) {
                        View r = p.getChildAt(j);
                        if (v == r)
                            continue;
                        else
                            ((RadioButton) r).setChecked(false);
                    }
                }

            });
        }
    }


    /**
     * This class will handle the api communication and data download
     */
    private class GetQuiz extends AsyncTask<String, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Your Quiz is Downloading...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(String... args) {
            com.example.android.quizapp.HttpHandler sh = new com.example.android.quizapp.HttpHandler();
            // Making a request to url and getting response
            String category = categories.get(args[0]);
            String difficulty = difficulties.get(args[1]);
            String url = "https://opentdb.com/api.php?amount=10&category=" + category +
                    "&difficulty=" + difficulty + "&type=multiple";
            String jsonStr = sh.makeServiceCall(url);
            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);


                    // Getting JSON Array node
                    JSONArray quizQuestions = jsonObj.getJSONArray("results");

                    // looping through All Questions
                    for (int i = 0; i < quizQuestions.length(); i++) {
                        JSONObject c = quizQuestions.getJSONObject(i);
                        questionsList.add(Html.fromHtml(c.getString("question")));
                        correctAnswerList.add(Html.fromHtml(c.getString("correct_answer")));
                        incorrectAnswersList.add(new Spanned[]{Html.fromHtml(c.getJSONArray("incorrect_answers").getString(0)),
                                Html.fromHtml(c.getJSONArray("incorrect_answers").getString(1)),
                                Html.fromHtml(c.getJSONArray("incorrect_answers").getString(2))});
                    }

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            for (int i = 0; i < questionsList.size(); i++) {
                createQuestion(i);
            }
            if (questionsList.size() == 0) {
                Toast.makeText(getApplicationContext(),
                        "Sorry, no such questions found in our database!",
                        Toast.LENGTH_LONG).show();
                prepareStartScreen();
            } else
                submitButton.setVisibility(View.VISIBLE);
        }
    }
}

