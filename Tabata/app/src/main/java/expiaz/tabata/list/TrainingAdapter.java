package expiaz.tabata.list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import expiaz.tabata.MainActivity;
import expiaz.tabata.PlayTrainingActivity;
import expiaz.tabata.R;
import expiaz.tabata.orm.entity.Training;
import expiaz.tabata.utils.TimeService;

/**
 * Created by expiaz on 07/11/2017.
 */

public class TrainingAdapter extends ArrayAdapter<Training> {

    private MainActivity main;

    public TrainingAdapter(Context context, List<Training> trainings) {
        super(context, 0, trainings);
        if(context instanceof MainActivity) {
            main = (MainActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must be MainActivity");
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.training, parent, false);
        }

        TrainingViewHolder viewHolder = (TrainingViewHolder) convertView.getTag();

        if(viewHolder == null){ // première hydratation
            viewHolder = new TrainingViewHolder();
            viewHolder.setName((TextView) convertView.findViewById(R.id.training_name));
            viewHolder.setTime((TextView) convertView.findViewById(R.id.training_specs_time));
            viewHolder.setReps((TextView) convertView.findViewById(R.id.training_specs_reps));
            viewHolder.setTimes((TextView) convertView.findViewById(R.id.training_specs_times));
            convertView.setTag(viewHolder);
        }

        // récupération du model
        final Training training = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        viewHolder.getName().setText(training.getName());
        viewHolder.getTime().setText(TimeService.toMMSS(training.getTime()));
        viewHolder.getReps().setText(String.valueOf(training.getTotalParts()));
        viewHolder.getTimes().setText(String.valueOf(training.getTimes()));

        // setup de listener pour l'action play d'un training
        ImageButton play = (ImageButton) convertView.findViewById(R.id.training_button_play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.playTraining(training);
            }
        });

        return convertView;
    }
}
