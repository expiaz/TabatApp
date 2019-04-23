package expiaz.tabata.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.db.circularcounter.CircularCounter;

import java.security.InvalidParameterException;

import expiaz.tabata.R;
import expiaz.tabata.orm.entity.Part;
import expiaz.tabata.orm.entity.Training;
import expiaz.tabata.utils.Callbackable;
import expiaz.tabata.utils.PartIterator;
import expiaz.tabata.utils.TimeService;
import expiaz.tabata.utils.Timer;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TimerFragment.TimerEventsListener} interface
 * to handle interaction events.
 * Use the {@link TimerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimerFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_TRAINING = "param1";

    private static final String STATE_TRAINING = "STATE_TRAINING";
    private static final String STATE_ITERATOR = "STATE_ITERATOR";
    private static final String STATE_PART_LEFT_TIME = "STATE_PART_LEFT_TIME";
    private static final String STATE_TABATA_LEFT_TIME = "STATE_TABATA_LEFT_TIME";
    private static final String STATE_TOTAL_LEFT_TIME = "STATE_TOTAL_LEFT_TIME";
    private static final String STATE_PAUSED = "STATE_PAUSED";

    private static final int INTERVAL_TICK = 100;

    private PartIterator partIterator;
    private Training training;

    private Timer timer;
    private CircularCounter counter;

    private int currentPartLeftTime;
    private int currentTabataLeftTime;
    private int totalLeftTime;

    private int totalTime;
    private int tabataTime;
    private int partTime;

    private boolean paused;

    private TimerEventsListener mListener;

    public TimerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param training the training.
     * @return A new instance of fragment TimerFragment.
     */
    public static TimerFragment newInstance(Training training) {
        TimerFragment fragment = new TimerFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TRAINING, training);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) { // re-création de l'instance
            training = savedInstanceState.getParcelable(STATE_TRAINING);
            partIterator = (PartIterator) savedInstanceState.getSerializable(STATE_ITERATOR);
            currentPartLeftTime = savedInstanceState.getInt(STATE_PART_LEFT_TIME);
            currentTabataLeftTime = savedInstanceState.getInt(STATE_TABATA_LEFT_TIME);
            totalLeftTime = savedInstanceState.getInt(STATE_TOTAL_LEFT_TIME);
            paused = savedInstanceState.getBoolean(STATE_PAUSED);
        } else if(getArguments() != null){ // création de l'instance
            training = getArguments().getParcelable(ARG_TRAINING);
            partIterator = (PartIterator) training.iterator();
            totalLeftTime = partIterator.getTotalTime() * 1000;
            currentTabataLeftTime = training.getTabataTime() * 1000;
            currentPartLeftTime = training.getFirstPart().getTime() * 1000;
            paused = false;
        } else {
            throw new InvalidParameterException("TimerFragment needs a training");
        }

        totalTime = partIterator.getTotalTime() * 1000;
        tabataTime = training.getTabataTime() * 1000;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_TRAINING, training);
        outState.putSerializable(STATE_ITERATOR, partIterator);
        outState.putInt(STATE_PART_LEFT_TIME, currentPartLeftTime);
        outState.putInt(STATE_TABATA_LEFT_TIME, currentTabataLeftTime);
        outState.putSerializable(STATE_TOTAL_LEFT_TIME, totalLeftTime);
        outState.putBoolean(STATE_PAUSED, paused);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_timer, container, false);
        this.counter = (CircularCounter) view.findViewById(R.id.counter);
        this.counter.setOnClickListener(this);

        this.counter.setRange(100)
                .setFirstWidth(25)
                .setFirstColor(getResources().getColor(R.color.counter_width_total))
                .setSecondWidth(50)
                .setSecondColor(getResources().getColor(R.color.counter_width_tabata))
                .setThirdWidth(70)
                .setThirdColor(getResources().getColor(R.color.counter_width_part_work))
                .setMetricSize(50)
                .setTextSize(100)
                .setBackgroundColor(getResources().getColor(R.color.counter_background));
        this.counter.setValues(100, 100, 100);

        // Inflate the layout for this fragment
        return view;
    }

    /**
     * lance le timer interne, commence les notifications et render le counter
     */
    @Override
    public void onStart() {
        super.onStart();

        // ce cycle de fonctionnement est normalement par le timer à chaque tick
        // mais lors de la re-création de la vue, le timer peut être en pause
        // il faut donc tout dessiner et notifier une première fois

        // première notification pour draw le temps dans le listener
        this.mListener.OnClockTick(totalLeftTime);
        // commence la première partie
        this.startPart(this.partIterator.current(), true);
        // dessine graphiquement le counter une première fois
        this.updateCounter(currentPartLeftTime, this.partIterator.current());
        // met ou non en pause le counter
        handlePause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TimerEventsListener) {
            mListener = (TimerEventsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TimerEventsListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        timer.stop();
        timer = null;
        counter = null;
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        paused = !paused;
        handlePause();
    }

    /**
     * met en pause ou restart le timer interne au fragment en fonction du boolean paused
     */
    private void handlePause(){
        if(paused){
            timer.pause();
            counter.setText("-");
            counter.setMetricText("paused");
            // force a re-draw
            counter.invalidate();
        } else {
            timer.restart();
        }
    }

    /**
     * commence la partie suivante à celle actuellement si il y en a une en appelant startPart,
     * sinon notifie le listener de la fin du training
     */
    private void nextPart(){
        if(this.partIterator.hasNext()) {
            this.startPart(this.partIterator.next());
        } else {
            // print end
            counter.setText(getResources().getString(R.string.play_no_time));
            counter.setMetricText(getResources().getString(R.string.play_parts_end));
            // re-draw
            counter.invalidate();
            mListener.OnTraningEnd();
        }
    }

    /**
     * helper pour start part sans keepState
     * @param part
     */
    private void startPart(Part part){
        this.startPart(part, false);
    }

    /**
     * commence une nouvelle partie dans le compteur, notifie le listener
     * @param part la partie actuelle
     * @param keepState reset les temps restants à leurs valeurs d'origine ou non (pour le restore instance)
     */
    private void startPart(final Part part, boolean keepState){

        // notification du listener
        mListener.OnNextPartStart(part);
        // on update ni le temps total ni celui du tabata, on reset celui de la partie actuelle
        this.counter.setValues(CircularCounter.UPDATE_NOOP, CircularCounter.UPDATE_NOOP, 100);
        // print the part name
        this.counter.setMetricText(part.getName());
        // print the part time
        this.counter.setText(TimeService.toMMSS(part.getTime()));

        // temps de la partie en millisecondes pour les calculs de pourcentages
        partTime = part.getTime() * 1000;

        // on fait les calculs sur l'id
        switch (part.getType()) {
            case Part.TABATA:
                // un nouveau tabata commence, on reset le temps restant du tabata actuel
                if(!keepState){
                    currentTabataLeftTime = tabataTime;
                }
                // no break, on change la couleur
            case Part.REST:
                // change la couleur en couleur de repos
                counter.changeThirdColor(getResources().getColor(R.color.counter_width_part_rest));
                break;

            case Part.WORK:
            default:
                // change la couleur en couleur de travail
                counter.changeThirdColor(getResources().getColor(R.color.counter_width_part_work));
                break;
        }

        if(!keepState){
            // mise a jour du state, reset tu temps restant de la partie actuelle
            currentPartLeftTime = partTime;
        }

        this.timer = new Timer(
                currentPartLeftTime,
                INTERVAL_TICK,
                new Callbackable<Void, Long>() { // on next tick
                    @Override
                    public Void call(Long left) {
                        updateCounter(left.intValue(), part);
                        return null;
                    }
                },
                null, // on pause
                new Callbackable<Void, Long>() { // on part end
                    @Override
                    public Void call(Long left) {
                        nextPart();
                        return null;
                    }
                }
        );

        timer.start();
    }

    /**
     * met à jour graphiquement le compteur avec le temps restant pour la partie donnée
     * @param left
     * @param part
     */
    private void updateCounter(int left, Part part) {
        // mises a jour du state
        currentPartLeftTime = left;
        totalLeftTime -= INTERVAL_TICK;
        currentTabataLeftTime -= INTERVAL_TICK;

        // (temps restant de la partie / temps total de la partie) *100 (en pourcentage)
        // une opérande est cast as float sinon le résultat serait toujours 1
        int partPercentage = Math.round((float) left / partTime * 100);
        // (temps restant du tabata / temps total du tabata) *100 (en pourcentage)
        int tabataPercentage = Math.round((float) currentTabataLeftTime / tabataTime * 100);
        // (temps restant du training / temps total du training) * 100 (en pourcentage)
        int fullPercentage = Math.round((float)totalLeftTime / totalTime * 100);

        // si nous sommes en phase de repos, la barre remonte au lieu de descendre ('reprise de forces')
        if(part.getType() == Part.REST) {
            partPercentage = 100 - partPercentage;
        }

        // update des pourcentages
        counter.setValues(fullPercentage, tabataPercentage, partPercentage);
        // update du temps affiché
        counter.setText(TimeService.toMMSS(currentPartLeftTime / 1000));
        // affichage du nom de la partie (pour overwrite une pause)
        counter.setMetricText(part.getName());

        mListener.OnClockTick(totalLeftTime);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface TimerEventsListener {
        /**
         * appellée à chaque début de partie de l'entrainement
         * @param part la partie suivante
         */
        void OnNextPartStart(Part part);

        /**
         * appellée à la fin de l'entrainenement
         */
        void OnTraningEnd();

        /**
         * appellée toutes les millisecondes
         * @param left
         */
        void OnClockTick(int left);
    }
}
