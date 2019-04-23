package expiaz.tabata;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.security.InvalidParameterException;

import expiaz.tabata.orm.entity.Training;
import expiaz.tabata.orm.repository.TrainingDAO;
import expiaz.tabata.utils.TimeService;

public class EditTrainingActivity extends AppCompatActivity {

    public final static String TRAINING_EDIT = "edit";

    public final static String STATE_NAME = "STATE_NAME";
    public final static String STATE_TRAINING = "STATE_TRAINING";

    private LinearLayout currentSelection;

    private Training edited;

    private TextView name;
    private TextView prepareTime;
    private TextView workTime;
    private TextView restTime;
    private TextView cycleTimes;
    private TextView tabataTimes;
    private TextView fullTime;

    private String wantedName;

    /**
     * helper pour cacher le clavier virtuel
     * @param activity
     * @param view
     */
    public static void hideSoftKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_training);

        if(savedInstanceState != null) { // re-création de l'instance
            edited = savedInstanceState.getParcelable(STATE_TRAINING);
            wantedName = savedInstanceState.getString(STATE_NAME);
        } else { // création de l'instance
            // on prend le mode pour lequel la vue à été appelé (soit create soit update)
            int mode = getIntent().getIntExtra("requestCode", MainActivity.RQC_UPDATE);
            edited = getIntent().getParcelableExtra(EditTrainingActivity.TRAINING_EDIT);
            if(edited == null) {
                if(mode == MainActivity.RQC_CREATE) {
                    // vue appellée en tant qu'ajout
                    edited = new Training();
                } else {
                    throw new InvalidParameterException("EditTrainingActivity needs mode and/or a training");
                }
            }
            wantedName = edited.getName();
        }

        name = (TextView) findViewById(R.id.training_edit_name);
        prepareTime = (TextView) findViewById(R.id.training_edit_prepare_time);
        workTime = (TextView) findViewById(R.id.training_edit_work_time);
        restTime = (TextView) findViewById(R.id.training_edit_rest_time);
        cycleTimes = (TextView) findViewById(R.id.training_edit_cycles_time);
        tabataTimes = (TextView) findViewById(R.id.training_edit_tabatas_time);
        fullTime = (TextView) findViewById(R.id.training_edit_time);

        // affichage du nom
        name.setText(wantedName);

        setupListeners();
        changeSelection(getDefaultSelection());
        render();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_NAME, wantedName);
        outState.putParcelable(STATE_TRAINING, edited);
    }

    /**
     * champs séléctionné par défaut
     * @return
     */
    private LinearLayout getDefaultSelection(){
        return (LinearLayout) findViewById(R.id.training_edit_prepare);
    }

    /**
     * met en place les click listeners pour le changement de ligne, l'ajout de temps et le nom
     */
    private void setupListeners() {

        // lorsque que la touche entrée est appuyée sur le nom, on sort de l'input
        name.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER) {
                    changeSelection(currentSelection);
                    return true;
                }
                return false;
            }
        });

        int[] ids = {
                R.id.training_edit_prepare,
                R.id.training_edit_work,
                R.id.training_edit_rest,
                R.id.training_edit_cycles,
                R.id.training_edit_tabatas
        };

        View.OnClickListener handleLayoutClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSelection((LinearLayout) v);
            }
        };

        for(int id : ids) {
            findViewById(id).setOnClickListener(handleLayoutClick);
        }

        View.OnClickListener handleButtonClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int amount = 0;
                if(v.getId() == R.id.training_edit_button_plus) {
                    amount = 1;
                } else {
                    amount = -1;
                }

                switch (currentSelection.getId()){
                    case R.id.training_edit_prepare:
                        edited.setTabataPrepare(edited.getTabataPrepare() + amount);
                        break;

                    case R.id.training_edit_work:
                        edited.setCycleWork(edited.getCycleWork() + amount);
                        break;

                    case R.id.training_edit_rest:
                        edited.setCycleRest(edited.getCycleRest() + amount);
                        break;

                    case R.id.training_edit_cycles:
                        edited.setCycleReps(edited.getCycleReps() + amount);
                        break;

                    case R.id.training_edit_tabatas:
                        edited.setTabataReps(edited.getTabataReps() + amount);
                        break;
                }

                render();
            }
        };

        findViewById(R.id.training_edit_button_plus).setOnClickListener(handleButtonClick);
        findViewById(R.id.training_edit_button_less).setOnClickListener(handleButtonClick);
    }

    /**
     * update la ligne selectionnée
     * @param selection la nouvelle selection
     */
    private void changeSelection(LinearLayout selection) {
        name.clearFocus();
        hideSoftKeyboard(EditTrainingActivity.this, selection);

        // reset l'ancienne selection
        if(currentSelection != null) {
            ((TextView) currentSelection.getChildAt(0)).setTypeface(null, Typeface.NORMAL);
            ((TextView) currentSelection.getChildAt(1)).setTypeface(null, Typeface.NORMAL);
        }
        // update la nouvelle selection
        currentSelection = selection;
        ((TextView) currentSelection.getChildAt(0)).setTypeface(null, Typeface.BOLD);
        ((TextView) currentSelection.getChildAt(1)).setTypeface(null, Typeface.BOLD);
    }

    /**
     * update les timers et le temps total de chaque partie
     */
    private void render() {
        prepareTime.setText(TimeService.toMMSS(edited.getTabataPrepare()));
        workTime.setText(TimeService.toMMSS(edited.getCycleWork()));
        restTime.setText(TimeService.toMMSS(edited.getCycleRest()));
        cycleTimes.setText(TimeService.toMMSS(edited.getCycleReps()));
        tabataTimes.setText(TimeService.toMMSS(edited.getTabataReps()));

        fullTime.setText(TimeService.toMMSS(edited.getTime()));
    }

    /**
     * helper pour afficher une erreur dans un toast
     * @param on
     * @param message
     */
    private void makeError(View on, String message) {
        //on.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        Toast error = Toast.makeText(EditTrainingActivity.this, message, Toast.LENGTH_SHORT);
        error.show();
    }

    /**
     * vérification des valeurs avant sauvegarde du training modifié
     */
    @Override
    public void onBackPressed() {

        TextView name = (TextView) findViewById(R.id.training_edit_name);

        wantedName = name.getText().toString().trim();

        // le nom ne peut pas être vide
        if(wantedName.length() == 0) {
            this.makeError(name, "Name required");
            return;
        }

        Training exists = TrainingDAO.getInstance().getByName(wantedName);
        // si un training de même nom existe, et qu'il est différent que l'actuel
        if(exists != null && !exists.getId().equals(edited.getId())) {
            this.makeError(name, "Name already exists");
            return;
        }

        edited.setName(wantedName);
        edited.save();

        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
