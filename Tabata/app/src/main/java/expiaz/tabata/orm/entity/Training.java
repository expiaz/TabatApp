package expiaz.tabata.orm.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

import java.util.Iterator;

import expiaz.tabata.utils.PartIterator;

/**
 * représente un training, avec des setters, beaucoup de getters et quelques méthodes de calculs sur ceux-ci
 */
public class Training extends SugarRecord implements Iterable<Part>, Parcelable {

    @Unique
    private String name = "";
    private int times = 0;
    private int tabataReps = 3;
    private int tabataPrepare = 20;
    private int cycleReps = 5;
    private int cycleWork = 10;
    private int cycleRest = 5;

    public Training(){

    }

    public Training(String name, int tabataPrepare, int tabataReps, int cycleReps, int cycleWork, int cycleRest){
        this.name = name;
        this.times = 0;
        this.tabataPrepare = tabataPrepare;
        this.tabataReps = tabataReps;
        this.cycleReps = cycleReps;
        this.cycleWork = cycleWork;
        this.cycleRest = cycleRest;
    }

    protected Training(Parcel in) {
        this.setId(in.readLong());
        name = in.readString();
        times = in.readInt();
        tabataReps = in.readInt();
        tabataPrepare = in.readInt();
        cycleReps = in.readInt();
        cycleWork = in.readInt();
        cycleRest = in.readInt();
    }

    public static final Creator<Training> CREATOR = new Creator<Training>() {
        @Override
        public Training createFromParcel(Parcel in) {
            return new Training(in);
        }

        @Override
        public Training[] newArray(int size) {
            return new Training[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.getId());
        dest.writeString(name);
        dest.writeInt(times);
        dest.writeInt(tabataReps);
        dest.writeInt(tabataPrepare);
        dest.writeInt(cycleReps);
        dest.writeInt(cycleWork);
        dest.writeInt(cycleRest);
    }

    private int ensureNonZero(int val) {
        return val > 0 ? val : 1;
    }

    @Override
    public Iterator<Part> iterator() {
        return new PartIterator(this);
    }

    public Part getFirstPart(){
        return new Part(Part.TABATA, "prepare", this.getTabataPrepare());
    }

    public int getTime(){
        return tabataReps * getTabataTime();
    }

    public int getTabataTime(){
        return tabataPrepare + (cycleReps * getCycleTime());
    }

    public int getCycleTime(){
        return cycleWork + cycleRest;
    }

    public int getTotalParts(){
        return tabataReps * cycleReps * 2 + tabataReps;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public int getTabataReps() {
        return tabataReps;
    }

    public void setTabataReps(int tabataReps) {
        this.tabataReps = this.ensureNonZero(tabataReps);
    }

    public int getTabataPrepare() {
        return tabataPrepare;
    }

    public void setTabataPrepare(int tabataPrepare) {
        this.tabataPrepare = this.ensureNonZero(tabataPrepare);
    }

    public int getCycleReps() {
        return cycleReps;
    }

    public void setCycleReps(int cycleReps) {
        this.cycleReps = this.ensureNonZero(cycleReps);
    }

    public int getCycleWork() {
        return cycleWork;
    }

    public void setCycleWork(int cycleWork) {
        this.cycleWork = this.ensureNonZero(cycleWork);
    }

    public int getCycleRest() {
        return cycleRest;
    }

    public void setCycleRest(int cycleRest) {
        this.cycleRest = this.ensureNonZero(cycleRest);
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
