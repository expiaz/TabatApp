package expiaz.tabata;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.security.InvalidParameterException;

import expiaz.tabata.fragment.TimerFragment;
import expiaz.tabata.orm.entity.Part;
import expiaz.tabata.orm.entity.Training;
import expiaz.tabata.utils.PartIterator;
import expiaz.tabata.utils.TimeService;

public class PlayTrainingActivity extends AppCompatActivity implements TimerFragment.TimerEventsListener {

    public final static String TRAINING_ARG = "edit";

    private final static String STATE_TRAINING = "STATE_TRAINING";
    private final static String STATE_PARTS_LEFT = "STATE_PARTS_LEFT";

    private Training playing;

    private LinearLayout currentPartContainer;
    private TextView currentPartName;
    private TextView currentPartTime;
    private LinearLayout nextPartContainer;
    private TextView nextPartName;
    private TextView nextPartTime;

    private TextView totalLeftTime;
    private TextView totalLeftParts;

    private int totalPartsLeft;
    private int totalTimeLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_training);

        if(savedInstanceState != null) { // re-création de l'instance
            playing = savedInstanceState.getParcelable(PlayTrainingActivity.STATE_TRAINING);
            totalPartsLeft = savedInstanceState.getInt(PlayTrainingActivity.STATE_PARTS_LEFT);
        } else {
            playing = getIntent().getParcelableExtra(PlayTrainingActivity.TRAINING_ARG);
            totalPartsLeft = playing.getTotalParts();

            // création du fragment seulement lors de la création de l'instance
            // car celui-ci est conservé entre les re-créations d'instances
            TimerFragment tf = TimerFragment.newInstance(playing);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container_timer, tf)
                    .show(tf)
                    .commit();
        }

        currentPartContainer = (LinearLayout) findViewById(R.id.play_parts_current);
        currentPartName = (TextView) findViewById(R.id.play_parts_current_name);
        currentPartTime = (TextView) findViewById(R.id.play_parts_current_time);
        nextPartContainer = (LinearLayout) findViewById(R.id.play_parts_next);
        nextPartName = (TextView) findViewById(R.id.play_parts_next_name);
        nextPartTime = (TextView) findViewById(R.id.play_parts_next_time);

        totalLeftTime = (TextView) findViewById(R.id.play_time_left);
        totalLeftParts = (TextView) findViewById(R.id.play_parts_left);

        // affichage des valeurs une première fois avant les updates suivants
        totalLeftParts.setText(String.valueOf(totalPartsLeft));
        totalLeftTime.setText(TimeService.toMMSS(totalTimeLeft));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(PlayTrainingActivity.STATE_TRAINING, this.playing);
        outState.putInt(PlayTrainingActivity.STATE_PARTS_LEFT, this.totalPartsLeft);
    }

    /**
     * permet la mise à jour de la la partie actuelle
     * @param part la partie qui vient de commencer
     */
    @Override
    public void OnNextPartStart(Part part) {
        // totalpartsleft est le nombre de parties restantes, commençant à 0
        // pour éviter les nombres négatifs (-1), nous ajoutons 1,
        // ainsi le nombre est plus significatif
        totalLeftParts.setText(String.valueOf(--totalPartsLeft + 1));

        // mise à jour de la partie actuelle dans l'encardré de gauche
        currentPartName.setText(part.getName());
        currentPartTime.setText(TimeService.toMMSS(part.getTime()));

        // màj de la couleur de fond en fonction de la partie actuelle
        if(part.getType() == Part.WORK) {
            currentPartContainer.setBackground(getResources().getDrawable(R.drawable.layout_work));
            currentPartTime.setTextColor(getResources().getColor(R.color.counter_width_part_work));
            currentPartName.setTextColor(getResources().getColor(R.color.counter_width_part_work));
        } else {
            currentPartContainer.setBackground(getResources().getDrawable(R.drawable.layout_rest));
            currentPartTime.setTextColor(getResources().getColor(R.color.counter_width_part_rest));
            currentPartName.setTextColor(getResources().getColor(R.color.counter_width_part_rest));
        }

        if(part.getNext() != null) { // il y a une prochaine partie
            Part next = part.getNext();
            // mise à jour de la partie suivante dans l'encardré de droite
            nextPartName.setText(next.getName());
            nextPartTime.setText(TimeService.toMMSS(next.getTime()));

            if(next.getType() == Part.WORK) {
                nextPartContainer.setBackground(getResources().getDrawable(R.drawable.layout_work));
                nextPartName.setTextColor(getResources().getColor(R.color.counter_width_part_work));
                nextPartTime.setTextColor(getResources().getColor(R.color.counter_width_part_work));
            } else {
                nextPartContainer.setBackground(getResources().getDrawable(R.drawable.layout_rest));
                nextPartName.setTextColor(getResources().getColor(R.color.counter_width_part_rest));
                nextPartTime.setTextColor(getResources().getColor(R.color.counter_width_part_rest));
            }

        } else { // c'est la dernière partie
            nextPartName.setText(R.string.play_parts_end);
            nextPartTime.setText(R.string.play_no_time);
            nextPartContainer.setBackground(getResources().getDrawable(R.drawable.layout_rest));
            nextPartName.setTextColor(getResources().getColor(R.color.counter_width_part_rest));
            nextPartTime.setTextColor(getResources().getColor(R.color.counter_width_part_rest));
        }
    }

    /**
     * update du temps restant à chaque tick du timer
     * @param left
     */
    @Override
    public void OnClockTick(int left) {
        totalTimeLeft = left / 1000;
        totalLeftTime.setText(TimeService.toMMSS(totalTimeLeft));
    }

    /**
     * permet de savoir si l'entrainement à bien été terminé et peut ainsi être comptabilisé
     */
    @Override
    public void OnTraningEnd() {
        // update le nombre de fois ou le training à été complété
        playing.setTimes(playing.getTimes() + 1);
        playing.save();
        // on a quitté correctement la vue
        setResult(RESULT_OK);
        this.finish();
    }
}
