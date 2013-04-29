
import cz.zcu.kiv.eeg.semweb.model.dbconnect.DbConnector;
import cz.zcu.kiv.eeg.semweb.model.dbconnect.OracleDbConnector;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class RelationalTest {

    private static final String tablePrefix = "A";
     


    public static void main (String [] args) {

        DbConnector dbc = null;

        try {

        //dbc = new VirtuosoDbConnector("model_semweb_a", "jdbc:virtuoso://localhost:1111", "dba", "dba");
        dbc = new OracleDbConnector("model_test_x", "jdbc:oracle:thin:@students.kiv.zcu.cz:1521:EEGERP", "EEGTEST", "JPERGLER");

        dbc.connect();
        Statement st = dbc.getRelationConn();

        int cycles = 10;

        int r1 = 0;
        int r2 = 0;
        int r3 = 0;

        int u1 = 0;
        int u2 = 0;
        int u3 = 0;

        int c1 = 0;
        int c2 = 0;
        int c3 = 0;

        int d1 = 0;
        int d2 = 0;
        int d3 = 0;

        for (int i = 0; i < cycles; i++) {
            r1 += readLoginUser(st, "username" + i);
        }

        for (int i = 0; i < cycles; i++) {
            r2 += readUserExperiment(st, "username" + i);
        }

        for (int i = 0; i < cycles; i++) {
            r3 += readUserExperiment(st, "username" + i);
        }

        System.out.println("R1: " + r1 + " R2: " + r2 + " R3: " + r3);

        // -----------------------------------------------------

        for (int i = 0; i < cycles; i++) {
            u1 += updateUserEmail(st, "username" + i, "e" + i);
        }

        for (int i = 0; i < cycles; i++) {
            u2 += updateUserExperimentTemperature(st, "username" + i);
        }

        for (int i = 0; i < cycles; i++) {
            u3 += updateUserExperimentWeather(st, "username" + i, "wt" + i, "wd" + i, i);
        }

        // -----------------------------------------------------

        for (int i = 0; i < cycles; i++) {
            c1 += createPerson(st, i);
        }

        for (int i = 0; i < cycles; i++) {
            c2 += createExperiment(st, "username" + i, i);
        }

        for (int i = 0; i < cycles; i++) {
            c3 += createGroupMember(st, "username" + i);
        }

        // -----------------------------------------------------

        for (int i = 0; i < cycles; i++) {
            d1 += removePerson(st, "username" + i);
        }

        for (int i = 0; i < cycles; i++) {
            d2 += removePersonsExperiments(st, "username" + i);
        }

        for (int i = 0; i < cycles; i++) {
            d3 += removePersonsEmail(st, "username" + i);
        }

        System.out.println("Read: ------------");
        System.out.println("R1: " + r1 + " R2: " + r2 + " R3: " + r3);

        System.out.println("Update: ------------");
        System.out.println("U1: " + u1 + " U2: " + u2 + " U3: " + u3);

        System.out.println("Create: ------------");
        System.out.println("C1: " + c1 + " C2: " + c2 + " C3: " + c3);

        System.out.println("Delete: ------------");
        System.out.println("D1: " + d1 + " D2: " + d2 + " D3: " + d3);


        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            dbc.disconnect();
        }
    }


    /**
     * Return time[ms] to get persons password by its username
     *
     * @param st relational connection instance
     * @param username person username
     * @return operatiron time[ms]
     */
    public static long readLoginUser(Statement st, String username) throws Exception {

        long startTime = System.currentTimeMillis();

        String query = "SELECT PASSWORD_ FROM " + tablePrefix + "PERSON WHERE USERNAME='" + username + "'";
        ResultSet rs = st.executeQuery(query);
  
        rs.next();
        String pwd = rs.getString("PASSWORD_");

        long endTime = System.currentTimeMillis();

        return endTime - startTime;
    }

    /**
     * Return time[ms] to get Experiment params Temeprature, StartTime and subject person surname
     * selected by specified person username
     *
     * @param st relational connection instance
     * @param username person username
     * @return operatiron time[ms]
     */
    public static long readUserExperiment(Statement st, String username) throws Exception {

        long startTime = System.currentTimeMillis();

        String query = "SELECT TEMPERATURE, START_TIME, B.SURNAME FROM " + tablePrefix + "PERSON A, " + tablePrefix + "PERSON B, " + tablePrefix + "EXPERIMENT WHERE A.USERNAME='" + username + "' AND A.PERSON_ID=OWNER_ID AND B.PERSON_ID=SUBJECT_PERSON_ID";

        ResultSet rs = st.executeQuery(query);
        rs.next();

        int temperature = rs.getInt("TEMPERATURE");
        Date date = rs.getDate("START_TIME");
        String surname = rs.getString("SURNAME");

        long endTime = System.currentTimeMillis();

        return endTime - startTime;
    }

    /**
     * Return time[ms] to get name of the group that is person who created reservation member of
     *
     * @param st relational connection instance
     * @param reservationUri reservation URI
     * @return operatiron time[ms]
     */
    public static long readReservationPersonGroupMembershipName(Statement st, String reservationUri) throws Exception {

        long startTime = System.currentTimeMillis();

        String query = "SELECT TITLE FROM " + tablePrefix + "RESERVATION, " + tablePrefix + "PERSON, " + tablePrefix + "RESEARCH_GROUP, " + tablePrefix + "RESEARCH_GROUP_MEMBERSHIP WHERE ";
        String cond1 = tablePrefix + "RESERVATION.RESERVATION_ID='" + reservationUri + "' AND ";
        String cond2 = tablePrefix + "RESERVATION.PERSON_ID=" + tablePrefix + "PERSON.PERSON_ID AND ";
        String cond3 = tablePrefix + "RESERVATION.PERSON_ID=" + tablePrefix + "RESEARCH_GROUP_MEMBERSHIP.PERSON_ID AND ";
        String cond4 = tablePrefix + "RESEARCH_GROUP_MEMBERSHIP.RESEARCH_GROUP_ID=" + tablePrefix + "RESEARCH_GROUP.RESEARCH_GROUP_ID";

        ResultSet rs = st.executeQuery(query + cond1 + cond2 + cond3 + cond4);
        rs.next();

        String title = rs.getString("TITLE");

        long endTime = System.currentTimeMillis();

        return endTime - startTime;
    }

    /**
     * Return time[ms] to update persons email selected by its username
     *
     * @param st relational connection instance
     * @param username person username
     * @return operatiron time[ms]
     */
    public static long updateUserEmail(Statement st, String username, String newEmail) throws Exception {



        long startTime = System.currentTimeMillis();

        String query = "UPDATE " + tablePrefix + "PERSON SET EMAIL='" + newEmail + "' WHERE USERNAME='" + username + "'";

        st.execute(query);

        long endTime = System.currentTimeMillis();

        return endTime - startTime;
    }

    /**
     * Return time[ms] to update user Experiment temperature param
     * selected by specified person username
     *
     * @param st relational connection instance
     * @param username person username
     * @return operatiron time[ms]
     */
    public static long updateUserExperimentTemperature(Statement st, String username) throws Exception {

        long startTime = System.currentTimeMillis();

        String query = "UPDATE " + tablePrefix + "EXPERIMENT SET TEMPERATURE='71' WHERE OWNER_ID=(SELECT PERSON_ID FROM " + tablePrefix + "PERSON WHERE USERNAME='" + username + "' AND  ROWNUM <= 1)";
        st.execute(query);

        long endTime = System.currentTimeMillis();

        return endTime - startTime;
    }

    /**
     * Return time[ms] to update user Experiment Weather param
     * selected by specified person username
     *
     * @param st relational connection instance
     * @param username person username
     * @return operatiron time[ms]
     */
    public static long updateUserExperimentWeather(Statement st, String username, String weatherTitle, String weatherDescription, int index) throws Exception {

        long startTime = System.currentTimeMillis();

        String query = "INSERT INTO " + tablePrefix + "WEATHER VALUES ('9999" + index + "', '" + weatherTitle + "', '" + weatherDescription + "')";
        st.execute(query);

        query = "UPDATE " + tablePrefix + "EXPERIMENT SET WEATHER_ID='9999" + index + "' WHERE OWNER_ID=(SELECT PERSON_ID FROM " + tablePrefix + "PERSON WHERE USERNAME='" + username + "' AND  ROWNUM <= 1)";
        st.execute(query);

        long endTime = System.currentTimeMillis();

        return endTime - startTime;
    }

    /**
     * Return time[ms] to create new person
     * All params has index suffix
     *
     * @param st relational connection instance
     * @param username person username
     * @return operatiron time[ms]
     */
    public static long createPerson(Statement st, int index) throws Exception {

        long startTime = System.currentTimeMillis();

        String query = "INSERT INTO " + tablePrefix + "PERSON VALUES ('" + index + "', 'username" + index + "', 'password" + index + "', 'email" + index + "', '" + index + "', 'givenname" + index + "', 'surname" + index + "', '01-NOV-01', '" + index + "')";
        st.execute(query);

        long endTime = System.currentTimeMillis();

        return endTime - startTime;
    }

    /**
     * Return time[ms] to create new experiment
     * All params has index suffix
     *
     * @param st relational connection instance
     * @param username person username
     * @return operatiron time[ms]
     */
    public static long createExperiment(Statement st, String username, int index) throws Exception {

        long startTime = System.currentTimeMillis();

        String query = "SELECT PERSON_ID FROM " + tablePrefix + "PERSON WHERE USERNAME='" + username + "'";
        ResultSet rs = st.executeQuery(query);
        rs.next();
        int id = rs.getInt("PERSON_ID");

        query = "INSERT INTO " + tablePrefix + "WEATHER VALUES ('9999" + index + "', 'wt" + index + "', 'wd" + index + "')";
        st.execute(query);

        query = "INSERT INTO " + tablePrefix + "EXPERIMENT VALUES ('" + index + "', '" + id + "', '01-NOV-01', '" + index + "', '9999" + index + "', '9" + index + "', 'note" + index + "', '" + index + "', '" + index + "')";
        st.execute(query);

        long endTime = System.currentTimeMillis();

        return endTime - startTime;
    }

    /**
     * Return time[ms] to add existing person as group member
     *
     * @param st relational connection instance
     * @param username person username
     * @return operatiron time[ms]
     */
    public static long createGroupMember(Statement st, String username) throws Exception {

        long startTime = System.currentTimeMillis();

        String query = "INSERT INTO " + tablePrefix + "RESEARCH_GROUP_MEMBERSHIP VALUES ('99', (SELECT PERSON_ID FROM " + tablePrefix + "PERSON WHERE USERNAME='" + username + "' AND ROWNUM <= 1))";
        st.execute(query);

        long endTime = System.currentTimeMillis();

        return endTime - startTime;
    }

    /**
     * Return time[ms] to remove person by username
     *
     * @param st relational connection instance
     * @param username person username
     * @return operatiron time[ms]
     */
    public static long removePerson(Statement st, String username) throws Exception {

        long startTime = System.currentTimeMillis();

        String query = "DELETE FROM " + tablePrefix + "PERSON WHERE USERNAME='" + username + "'";
        st.execute(query);

        long endTime = System.currentTimeMillis();

        return endTime - startTime;
    }

    /**
     * Return time[ms] to remove all users experiments
     *
     * @param st relational connection instance
     * @param username person username
     * @return operatiron time[ms]
     */
    public static long removePersonsExperiments(Statement st, String username) throws Exception {

        long startTime = System.currentTimeMillis();

        String query = "DELETE FROM " + tablePrefix + "EXPERIMENT WHERE OWNER_ID=(SELECT PERSON_ID FROM " + tablePrefix + "PERSON WHERE USERNAME='" + username + "')";
        st.execute(query);

        long endTime = System.currentTimeMillis();

        return endTime - startTime;
    }

    /**
     * Return time[ms] to remove persons email
     *
     * @param st relational connection instance
     * @param username person username
     * @return operatiron time[ms]
     */
    public static long removePersonsEmail(Statement st, String username) throws Exception {

        long startTime = System.currentTimeMillis();

        String query = "UPDATE " + tablePrefix + "PERSON SET EMAIL=NULL WHERE USERNAME='" + username + "'";
        st.execute(query);

        long endTime = System.currentTimeMillis();

        return endTime - startTime;
    }





}
