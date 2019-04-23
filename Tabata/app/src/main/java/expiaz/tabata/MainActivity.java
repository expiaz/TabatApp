package expiaz.tabata;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import expiaz.tabata.orm.entity.Training;
import expiaz.tabata.list.TrainingAdapter;
import expiaz.tabata.orm.repository.TrainingDAO;

public class MainActivity extends AppCompatActivity {

    public static final int RQC_CREATE = 1;
    public static final int RQC_UPDATE = 2;
    public static final int RQC_PLAY = 2;

    private ListView trainingList;
    public List<Training> trainings;
    private ArrayAdapter trainingAdapter;

    private TrainingDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        trainingList = (ListView) findViewById(R.id.list_training);

        // récupération des trainings
        dao = TrainingDAO.getInstance();
        trainings = this.dao.getTrainings();
        // création de l'adapter pour la listview
        trainingAdapter = new TrainingAdapter(MainActivity.this, trainings);
        trainingList.setAdapter(trainingAdapter);

        // mise en place des hooks
        setupAddAction();
        setupEditAction();
        setupDeleteAction();
    }

    /**
     * ajoute le listener au click sur le bouton d'ajout
     */
    private void setupAddAction(){
        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.training_button_add);

        // styles du boutton d'ajout
        addButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));

        // handler de l'ajout
        View.OnClickListener addHandler = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTraining();
            }
        };

        addButton.setOnClickListener(addHandler);
    }

    /**
     * ajoute le listener au clique sur chaque item de la liste de trainings
     */
    private void setupEditAction(){
        trainingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Training training = (Training) trainingList.getItemAtPosition(position);
                editTraining(training);
            }

        });
    }

    /**
     * ajoute le listener au clique long sur chaque item de la liste de trainings
     */
    private void setupDeleteAction(){

        AdapterView.OnItemLongClickListener deleteHandler = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Training training = (Training) trainingList.getItemAtPosition(position);

                // Handlers pour les actions de l'alert dialog
                DialogInterface.OnClickListener onAckHandler = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeTraining(training);
                    }
                };
                DialogInterface.OnClickListener onNackHandler = null;

                // on créer une boite de dialogue
                AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                // on attribut un titre à notre boite de dialogue
                adb.setTitle(getResources().getString(R.string.delete_training_message).replace("%name", training.getName()));
                // on indique que l'on veut le bouton ok à notre boite de dialogue
                adb.setPositiveButton(R.string.delete_training_ack, onAckHandler);
                adb.setNegativeButton(R.string.delete_training_nack, onNackHandler);

                adb.show();

                // on consomme l'event
                return true;
            }
        };

        trainingList.setOnItemLongClickListener(deleteHandler);
    }

    /**
     * mise à jour de la liste par rapport à la base de données
     */
    private void updateList(){
        // update de la liste
        trainingAdapter.clear();
        trainingAdapter.addAll(dao.getTrainings());
        trainingAdapter.notifyDataSetChanged();
    }

    /**
     * ouvre l'activité pour jouer le training passé
     * @param training
     */
    public void playTraining(Training training) {
        Intent intent = new Intent(MainActivity.this, PlayTrainingActivity.class);
        intent.putExtra(PlayTrainingActivity.TRAINING_ARG, training);
        startActivityForResult(intent, MainActivity.RQC_PLAY);
    }

    /**
     * ouvre l'activité pour ajouter un training
     */
    public void addTraining(){
        Intent toCreate = new Intent(MainActivity.this, EditTrainingActivity.class);
        startActivityForResult(toCreate, MainActivity.RQC_CREATE);
    }

    /**
     * ouvre l'activité pour éditer le training passé
     * @param training
     */
    public void editTraining(Training training) {
        Intent toEdit = new Intent(MainActivity.this, EditTrainingActivity.class);
        toEdit.putExtra(EditTrainingActivity.TRAINING_EDIT, training);
        startActivityForResult(toEdit, MainActivity.RQC_UPDATE);
    }

    /**
     * supprimer le training passé du model et de la base de données
     * @param training
     */
    public void removeTraining(Training training) {
        // suppression du training dans le model
        trainingAdapter.remove(training);
        trainingAdapter.notifyDataSetChanged();
        // suppression du training dans la bdd
        training.delete();
    }


    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        // ajoute le code pour la permettre à vue d'édition
        // qui sert aussi pour l'ajout de savoir quelle action éxécuter
        intent.putExtra("requestCode", requestCode);
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && (
            requestCode == RQC_PLAY || requestCode == RQC_UPDATE || requestCode == RQC_CREATE
        )) {
            updateList();
        }
    }

}
