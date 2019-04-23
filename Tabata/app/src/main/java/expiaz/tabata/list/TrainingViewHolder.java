package expiaz.tabata.list;

import android.widget.TextView;

import expiaz.tabata.orm.entity.Training;

/**
 * Created by expiaz on 07/11/2017.
 */

public class TrainingViewHolder {
    private TextView name;
    private TextView time;
    private TextView reps;
    private TextView times;

    public TrainingViewHolder() {
    }


    public TextView getName() {
        return name;
    }

    public void setName(TextView name) {
        this.name = name;
    }

    public TextView getTime() {
        return time;
    }

    public void setTime(TextView time) {
        this.time = time;
    }

    public TextView getReps() {
        return reps;
    }

    public void setReps(TextView reps) {
        this.reps = reps;
    }

    public TextView getTimes() {
        return times;
    }

    public void setTimes(TextView times) {
        this.times = times;
    }
}
