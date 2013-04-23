
import com.hp.hpl.jena.ontology.OntModelSpec;
import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.Item;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.UriItem;
import cz.zcu.kiv.eeg.semweb.model.dbconnect.DbConnector;
import cz.zcu.kiv.eeg.semweb.model.dbconnect.VirtuosoDbConnector;
import cz.zcu.kiv.eeg.semweb.model.search.Condition;
import cz.zcu.kiv.eeg.semweb.model.search.PropertyValEqCondition;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class SimpleTest {

    public static final String nsPrefix = "http://cz.zcu.kiv.eeg#";


    public static void main (String [] args) {

        PortalModel pm = null;

        try {

        DbConnector dbc = new VirtuosoDbConnector("model_semweb_a", "jdbc:virtuoso://localhost:1111", "dba", "dba");
        //DbConnector dbc = new OracleDbConnector("model_test_x", "jdbc:oracle:thin:@students.kiv.zcu.cz:1521:EEGERP", "EEGTEST", "JPERGLER");


        //pm = new PortalModel(dbc, "http://cz.zcu.kiv.eeg#", "EEG_", OntModelSpec.OWL_MEM_RDFS_INF);
        pm = new PortalModel(dbc, "http://cz.zcu.kiv.eeg#", "EEG_", OntModelSpec.RDFS_MEM);
        pm.connect();




        for (int i = 0; i < 1; i++) {
            System.out.println("Time: " + removePersonsEmail(pm, "userName3"));
        }


//        OntModel jenaModel = pm.getOntModel();
//        jenaModel.createClass("www.claska.cz");
//
//        for (int i = 0; i < 100; i ++) {
//            jenaModel.createClass("www.claska.cz#C" + i);
//
//            if (i % 10 == 0) {
//                System.out.println(i);
//            }
//        }
//
//        System.out.println(pm.getOntModel().getOntClass("www.claska.cz").getURI());
//
//
//        System.out.println(jenaModel.listStatements().toList().size());

        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            pm.close();
        }
    }


    /**
     * Return time[ms] to get persons password by its username
     *
     * @param pm PortalModel instance
     * @param username person username
     * @return operatiron time[ms]
     */
    public static long readLoginUser(PortalModel pm, String username) throws Exception {

        Condition c = new PropertyValEqCondition(new UriItem(nsPrefix + "person/researcher/username", pm), username);

        long startTime = System.currentTimeMillis();

        Item person = pm.listClassInstances(nsPrefix + "person/researcher", c).get(0);
        String pwd = person.getAsUri().getPropertyVal(nsPrefix + "person/researcher/password").getAsLiteral().getValue().toString();

        long endTime  = System.currentTimeMillis();
        //System.out.println(pwd);
        return endTime - startTime;
    }

    /**
     * Return time[ms] to get Experiment params Temeprature, StartTime and subject person surname
     * selected by specified person username
     *
     * @param pm PortalModel instance
     * @param username person username
     * @return operatiron time[ms]
     */
    public static long readUserExperiment(PortalModel pm, String username) throws Exception {

        Condition c1 = new PropertyValEqCondition(new UriItem(nsPrefix + "person/researcher/username", pm), username);

        long startTime = System.currentTimeMillis();

        String personUri = pm.listClassInstances(nsPrefix + "person/researcher", c1).get(0).getAsUri().getUri();
        Condition c = new PropertyValEqCondition(new UriItem(nsPrefix + "experiment/owner_person", pm), personUri);

        Item experiment = pm.listClassInstances(nsPrefix + "experiment", c).get(0);
                
        String temperatureExp = experiment.getAsUri().getPropertyVal(nsPrefix + "experiment/temperature").getAsLiteral().getValue().toString();
        String startTimeExp = experiment.getAsUri().getPropertyVal(nsPrefix + "experiment/start_time").getAsLiteral().getValue().toString();
        String subjectSurnameExp = experiment.getAsUri().getPropertyVal(nsPrefix + "experiment/subject_person").
                getAsUri().getPropertyVal(nsPrefix + "person/test_subject/surname").getAsLiteral().getValue().toString();
        
        long endTime  = System.currentTimeMillis();
        //System.out.println("Result: " + temperatureExp + " - " + startTimeExp + " - " + subjectSurnameExp);
        return endTime - startTime;
    }



    /**
     * Return time[ms] to get name of the group that is person who created reservation member of
     *
     * @param pm PortalModel instance
     * @param reservationUri reservation URI
     * @return operatiron time[ms]
     */
    public static long readReservationPersonGroupMembershipName(PortalModel pm, String reservationUri) throws Exception {

        long startTime = System.currentTimeMillis();

        Item reservation = pm.getIndividualByUri(reservationUri);
        Item person = reservation.getAsUri().getPropertyVal(nsPrefix + "reservation/person");
        String groupName = person.getAsUri().getPropertyVal(nsPrefix + "person/researcher/group_member").getAsUri().getPropertyVal(nsPrefix + "research_group/title").getAsLiteral().getValue().toString();

        long endTime  = System.currentTimeMillis();
        System.out.println("Result: " + groupName);
        return endTime - startTime;
    }



    /**
     * Return time[ms] to update persons email selected by its username
     *
     * @param pm PortalModel instance
     * @param username person username
     * @return operatiron time[ms]
     */
    public static long updateUserEmail(PortalModel pm, String username, String newEmail) throws Exception {

        Condition c = new PropertyValEqCondition(new UriItem(nsPrefix + "person/researcher/username", pm), username);

        long startTime = System.currentTimeMillis();

        Item person = pm.listClassInstances(nsPrefix + "person/researcher", c).get(0);
        person.getAsUri().getPropertyVal(nsPrefix + "person/researcher/email").getAsLiteral().updateValue(newEmail);

        long endTime  = System.currentTimeMillis();

        return endTime - startTime;
    }


    /**
     * Return time[ms] to update user Experiment Weather param
     * selected by specified person username
     *
     * @param pm PortalModel instance
     * @param username person username
     * @return operatiron time[ms]
     */
    public static long updateUserExperimentWeather(PortalModel pm, String username, String weatherTitle, String weatherDescription) throws Exception {

        Condition c1 = new PropertyValEqCondition(new UriItem(nsPrefix + "person/researcher/username", pm), username);

        long startTime = System.currentTimeMillis();

        UriItem weather = pm.createClassInstance(nsPrefix + "weather");
        weather.addPropertyValue(nsPrefix + "weather/title", weatherTitle);
        weather.addPropertyValue(nsPrefix + "weather/description", weatherDescription);

        String personUri = pm.listClassInstances(nsPrefix + "person/researcher", c1).get(0).getAsUri().getUri();
        Condition c = new PropertyValEqCondition(new UriItem(nsPrefix + "experiment/owner_person", pm), personUri);

        Item experiment = pm.listClassInstances(nsPrefix + "experiment", c).get(0);

        experiment.getAsUri().updatePropertyValue(nsPrefix + "experiment/weather", experiment.getAsUri().getPropertyVal(nsPrefix + "experiment/weather").getAsUri().getUri(), weather.getUri());

        long endTime  = System.currentTimeMillis();

        return endTime - startTime;
    }

    /**
     * Return time[ms] to update user Experiment temperature param
     * selected by specified person username
     *
     * @param pm PortalModel instance
     * @param username person username
     * @return operatiron time[ms]
     */
    public static long updateUserExperimentTemperature(PortalModel pm, String username) throws Exception {

        Condition c1 = new PropertyValEqCondition(new UriItem(nsPrefix + "person/researcher/username", pm), username);

        long startTime = System.currentTimeMillis();

        String personUri = pm.listClassInstances(nsPrefix + "person/researcher", c1).get(0).getAsUri().getUri();
        Condition c = new PropertyValEqCondition(new UriItem(nsPrefix + "experiment/owner_person", pm), personUri);

        Item experiment = pm.listClassInstances(nsPrefix + "experiment", c).get(0);

        experiment.getAsUri().getPropertyVal(nsPrefix + "experiment/temperature").getAsLiteral().updateValue(71);

        long endTime  = System.currentTimeMillis();

        return endTime - startTime;
    }


    /**
     * Return time[ms] to create new person
     * All params has index suffix
     *
     * @param pm PortalModel instance
     * @param username person username
     * @return operatiron time[ms]
     */
    public static long createPerson(PortalModel pm, int index) throws Exception {

        long startTime = System.currentTimeMillis();

        UriItem person = pm.createClassInstance(nsPrefix + "person/researcher");

        person.addPropertyValue(nsPrefix + "person/researcher/given_name", "Johny " + index);
        person.addPropertyValue(nsPrefix + "person/researcher/surname", "Smith " + index);
        person.addPropertyValue(nsPrefix + "person/researcher/date_birth", "2222-12-12");

        person.addPropertyValue(nsPrefix + "person/researcher/education_level", nsPrefix + "education_level/instance0");
        person.addPropertyValue(nsPrefix + "person/researcher/facebook_id", "12541254");
        person.addPropertyValue(nsPrefix + "person/researcher/group_member", nsPrefix + "research_group/instance1");


        long endTime  = System.currentTimeMillis();

        return endTime - startTime;
    }


    /**
     * Return time[ms] to create new experiment
     * All params has index suffix
     *
     * @param pm PortalModel instance
     * @param username person username
     * @return operatiron time[ms]
     */
    public static long createExperiment(PortalModel pm, String username, int index) throws Exception {

        long startTime = System.currentTimeMillis();

        UriItem experiment = pm.createClassInstance(nsPrefix + "experiment");

        experiment.addPropertyValue(nsPrefix + "experiment/digitization", nsPrefix + "digitization/instance0");
        experiment.addPropertyValue(nsPrefix + "experiment/research_group", nsPrefix + "research_group/instance0");
        experiment.addPropertyValue(nsPrefix + "experiment/software", nsPrefix + "software/instance1");
        experiment.addPropertyValue(nsPrefix + "experiment/scenario", nsPrefix + "scenario/instance0");
        experiment.addPropertyValue(nsPrefix + "experiment/environment_note", "note" + index);

        UriItem weather = pm.createClassInstance(nsPrefix + "weather");
        weather.addPropertyValue(nsPrefix + "weather/title", "Weatherr " + index);
        weather.addPropertyValue(nsPrefix + "weather/description", "Descr " + index);

        experiment.addPropertyValue(nsPrefix + "experiment/weather", weather.getUri());

        Condition c = new PropertyValEqCondition(new UriItem(nsPrefix + "person/researcher/username", pm), username);
        Item person = pm.listClassInstances(nsPrefix + "person/researcher", c).get(0);
        experiment.addPropertyValue(nsPrefix + "experiment/owner_person", person.getAsUri().getUri());

        long endTime  = System.currentTimeMillis();

        return endTime - startTime;
    }


    /**
     * Return time[ms] to add existing person as group member
     *
     * @param pm PortalModel instance
     * @param username person username
     * @return operatiron time[ms]
     */
    public static long createGroupMember(PortalModel pm, String username) throws Exception {

        long startTime = System.currentTimeMillis();

        Condition c = new PropertyValEqCondition(new UriItem(nsPrefix + "person/researcher/username", pm), username);
        Item person = pm.listClassInstances(nsPrefix + "person/researcher", c).get(0);

        person.getAsUri().addPropertyValue(nsPrefix + "person/researcher/group_member", nsPrefix + "research_group/instance1");

        long endTime  = System.currentTimeMillis();

        return endTime - startTime;
    }

    /**
     * Return time[ms] to remove person by username
     *
     * @param pm PortalModel instance
     * @param username person username
     * @return operatiron time[ms]
     */
    public static long removePerson(PortalModel pm, String username) throws Exception {

        long startTime = System.currentTimeMillis();

        Condition c = new PropertyValEqCondition(new UriItem(nsPrefix + "person/researcher/username", pm), username);
        Item person = pm.listClassInstances(nsPrefix + "person/researcher", c).get(0);

        pm.removeIndividual(person.getAsUri().getUri());

        long endTime  = System.currentTimeMillis();

        return endTime - startTime;
    }


    /**
     * Return time[ms] to remove all users experiments
     *
     * @param pm PortalModel instance
     * @param username person username
     * @return operatiron time[ms]
     */
    public static long removePersonsExperiments(PortalModel pm, String username) throws Exception {

        long startTime = System.currentTimeMillis();

        Condition c = new PropertyValEqCondition(new UriItem(nsPrefix + "person/researcher/username", pm), username);
        Item person = pm.listClassInstances(nsPrefix + "person/researcher", c).get(0);


        Condition c2 = new PropertyValEqCondition(new UriItem(nsPrefix + "experiment/owner_person", pm), person.getAsUri().getUri());

        Item experiment = pm.listClassInstances(nsPrefix + "experiment", c2).get(0);

        pm.removeIndividual(experiment.getAsUri().getUri());

        long endTime  = System.currentTimeMillis();

        return endTime - startTime;
    }

     /**
     * Return time[ms] to remove persons email
     *
     * @param pm PortalModel instance
     * @param username person username
     * @return operatiron time[ms]
     */
    public static long removePersonsEmail(PortalModel pm, String username) throws Exception {

        long startTime = System.currentTimeMillis();

        Condition c = new PropertyValEqCondition(new UriItem(nsPrefix + "person/researcher/username", pm), username);
        Item person = pm.listClassInstances(nsPrefix + "person/researcher", c).get(0);

        person.getAsUri().getPropertyVal(nsPrefix + "person/researcher/email").getAsLiteral().removeValue();

        long endTime  = System.currentTimeMillis();

        return endTime - startTime;
    }




}
