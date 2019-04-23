package expiaz.tabata.orm.repository;

import java.util.List;

import expiaz.tabata.orm.entity.Training;

public class TrainingDAO {

    private static TrainingDAO instance = new TrainingDAO();

    public static TrainingDAO getInstance() {
        return instance;
    }

    private TrainingDAO() {
    }

    public Training getByName(String name) throws IllegalArgumentException {
        List<Training> e = Training.find(Training.class, "name = ?", name);
        if(e.size() == 0){
            return null;
        }
        return e.get(0);
    }

    public List<Training> getTrainings() {
        return Training.listAll(Training.class);
    }

}
