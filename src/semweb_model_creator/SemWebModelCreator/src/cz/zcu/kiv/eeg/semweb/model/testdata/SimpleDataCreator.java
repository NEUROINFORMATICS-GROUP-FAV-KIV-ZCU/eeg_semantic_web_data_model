package cz.zcu.kiv.eeg.semweb.model.testdata;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class SimpleDataCreator {

    private List<Triple> testData;

    public SimpleDataCreator() {
        testData = new ArrayList<Triple>();
        generateSimpleData();
    }

    public List<Triple> getData() {

        return testData;
    }

    private void generateSimpleData() {

        for (int number = 0; number < 1; number++) {
            testData.addAll(createAnalysis(number));
            testData.addAll(createArtefact(number));
            testData.addAll(createArtefactRemMeth(number));
            testData.addAll(createArticle(number));
            testData.addAll(createArticleComment(number));
            testData.addAll(createDataFile(number));
            testData.addAll(createDigitization(number));
            testData.addAll(createDisease(number));
            testData.addAll(createEducationLevel(number));
            testData.addAll(createElectrodeConfiguration(number));
            testData.addAll(createElectrodeFix(number));
            testData.addAll(createElectrodeLocation(number));
            testData.addAll(createElectrodeSystem(number));
            testData.addAll(createElectrodeType(number));
            testData.addAll(createExperiment(number));

            testData.addAll(createHardware(number));
            testData.addAll(createHistory(number));
            testData.addAll(createPersonResearcher(number));
            testData.addAll(createPersonTestSubject(number));
            testData.addAll(createPharmaceutical(number));
            testData.addAll(createProjectType(number));
            testData.addAll(createResearchGroup(number));
            testData.addAll(createReservation(number));
            testData.addAll(createScenario(number));
            testData.addAll(createSoftware(number));
            testData.addAll(createStimulus(number));
            testData.addAll(createSubjectGroup(number));
            testData.addAll(createWeather(number));

        }





    }

    private List<Triple> createArticle(int number) {

        List<Triple> articleTriples = new ArrayList<Triple>();

        articleTriples.add(new Triple("article/instance" + number, "article/author", "person/researcher/instance" + number));
        articleTriples.add(new Triple("article/instance" + number, "article/research_group", "research_group/instance" + number));
        articleTriples.add(new Triple("article/instance" + number, "article/title", "TitleValue" + number));
        articleTriples.add(new Triple("article/instance" + number, "article/time", "2001-10-26T21:32:52"));
        articleTriples.add(new Triple("article/instance" + number, "article/subscription", "person/researcher/instance" + number));

        return articleTriples;
    }

    private List<Triple> createArticleComment(int number) {

        List<Triple> articleCommentTriples = new ArrayList<Triple>();

        articleCommentTriples.add(new Triple("article_comment/instance" + number, "article_comment/article", "article/instance" + number));
        articleCommentTriples.add(new Triple("article_comment/instance" + number, "article_comment/parent_comment", "article_comment/instance" + number));
        articleCommentTriples.add(new Triple("article_comment/instance" + number, "article_comment/time", "2001-10-26T21:32:52"));
        articleCommentTriples.add(new Triple("article_comment/instance" + number, "article_comment/person", "person/researcher/instance" + number));

        return articleCommentTriples;
    }

    private List<Triple> createReservation(int number) {

        List<Triple> reservationTriples = new ArrayList<Triple>();

        reservationTriples.add(new Triple("reservation/instance" + number, "reservation/person", "person/researcher/instance" + number));
        reservationTriples.add(new Triple("reservation/instance" + number, "reservation/creation_time", "2001-10-26T21:32:52"));
        reservationTriples.add(new Triple("reservation/instance" + number, "reservation/research_group", "research_group/instance" + number));
        reservationTriples.add(new Triple("reservation/instance" + number, "reservation/start_time", "2001-10-26T21:32:52"));
        reservationTriples.add(new Triple("reservation/instance" + number, "reservation/end_time", "2001-10-26T21:32:52"));

        return reservationTriples;
    }

    private List<Triple> createPersonResearcher(int number) {

        List<Triple> researcherTriples = new ArrayList<Triple>();

        researcherTriples.add(new Triple("person/researcher/instance" + number, "person/given_name", "givenName" + number));
        researcherTriples.add(new Triple("person/researcher/instance" + number, "person/surname", "surname" + number));
        researcherTriples.add(new Triple("person/researcher/instance" + number, "person/date_birth", "2001-10-26"));
        researcherTriples.add(new Triple("person/researcher/instance" + number, "person/email", "email" + number));
        researcherTriples.add(new Triple("person/researcher/instance" + number, "person/phone_number", "1234" + number));
        researcherTriples.add(new Triple("person/researcher/instance" + number, "person/note", "Note" + number));
        researcherTriples.add(new Triple("person/researcher/instance" + number, "person/education_level", "education_level/instance" + number));

        researcherTriples.add(new Triple("person/researcher/instance" + number, "person/researcher/username", "userName" + number));
        researcherTriples.add(new Triple("person/researcher/instance" + number, "person/researcher/password", "password" + number));
        researcherTriples.add(new Triple("person/researcher/instance" + number, "person/researcher/authority", "authX" + number));
        researcherTriples.add(new Triple("person/researcher/instance" + number, "person/researcher/default_group", "research_group/instance" + number));
        researcherTriples.add(new Triple("person/researcher/instance" + number, "person/researcher/registration_date", "2001-10-26"));
        researcherTriples.add(new Triple("person/researcher/instance" + number, "person/researcher/confirmed", "1"));
        researcherTriples.add(new Triple("person/researcher/instance" + number, "person/researcher/authentication", "authent" + number));
        researcherTriples.add(new Triple("person/researcher/instance" + number, "person/researcher/facebook_id", "145" + number));
        researcherTriples.add(new Triple("person/researcher/instance" + number, "person/researcher/article_group_subscription", "research_group/instance" + number));
        researcherTriples.add(new Triple("person/researcher/instance" + number, "person/researcher/group_member", "research_group/instance" + number * 2));
        researcherTriples.add(new Triple("person/researcher/instance" + number, "person/researcher/group_member_admin", "research_group/instance" + number * 3));
        researcherTriples.add(new Triple("person/researcher/instance" + number, "person/researcher/group_member_experimenter", "research_group/instance" + number * 4));
        researcherTriples.add(new Triple("person/researcher/instance" + number, "person/researcher/group_member_reader", "research_group/instance" + number * 5));

        return researcherTriples;
    }

    private List<Triple> createPersonTestSubject(int number) {

        List<Triple> tetsSubjectTriples = new ArrayList<Triple>();

        tetsSubjectTriples.add(new Triple("person/test_subject/instance" + number, "person/given_name", "givenName" + number));
        tetsSubjectTriples.add(new Triple("person/test_subject/instance" + number, "person/surname", "surname" + number));
        tetsSubjectTriples.add(new Triple("person/test_subject/instance" + number, "person/date_birth", "2001-10-22"));
        tetsSubjectTriples.add(new Triple("person/test_subject/instance" + number, "person/email", "email" + number));
        tetsSubjectTriples.add(new Triple("person/test_subject/instance" + number, "person/phone_number", "1234" + number));
        tetsSubjectTriples.add(new Triple("person/test_subject/instance" + number, "person/note", "Note" + number));
        tetsSubjectTriples.add(new Triple("person/test_subject/instance" + number, "person/education_level", "education_level/instance" + number));

        tetsSubjectTriples.add(new Triple("person/test_subject/instance" + number, "person/test_subject/gender", "gender" + number));
        tetsSubjectTriples.add(new Triple("person/test_subject/instance" + number, "person/test_subject/laterality", "laterality" + number));

        return tetsSubjectTriples;
    }

    private List<Triple> createEducationLevel(int number) {

        List<Triple> eduLevelTriples = new ArrayList<Triple>();

        eduLevelTriples.add(new Triple("education_level/instance" + number, "education_level/title", "Title" + number));
        eduLevelTriples.add(new Triple("education_level/instance" + number, "education_level/is_default", "isDefault" + number));
        eduLevelTriples.add(new Triple("education_level/instance" + number, "education_level/research_group", "research_group/instance" + number));

        return eduLevelTriples;
    }

    private List<Triple> createResearchGroup(int number) {

        List<Triple> researchGroupTriples = new ArrayList<Triple>();

        researchGroupTriples.add(new Triple("research_group/instance" + number, "research_group/owner", "person/researcher/instance" + number));
        researchGroupTriples.add(new Triple("research_group/instance" + number, "research_group/title", "title" + number));
        researchGroupTriples.add(new Triple("research_group/instance" + number, "research_group/description", "description" + number));

        return researchGroupTriples;
    }

    private List<Triple> createHistory(int number) {

        List<Triple> historyTriples = new ArrayList<Triple>();

        historyTriples.add(new Triple("history/instance" + number, "history/experiment", "experiment/instance" + number));
        historyTriples.add(new Triple("history/instance" + number, "history/scenario", "scenarion/instance" + number));
        historyTriples.add(new Triple("history/instance" + number, "history/person", "person/researcher/instance" + number));
        historyTriples.add(new Triple("history/instance" + number, "history/date_of_download", "2001-10-26T21:32:52"));
        historyTriples.add(new Triple("history/instance" + number, "history/data_file", "data_file/instance" + number));

        return historyTriples;
    }

    private List<Triple> createExperiment(int number) {

        List<Triple> experimentTriples = new ArrayList<Triple>();

        experimentTriples.add(new Triple("experiment/instance" + number, "experiment/scenario", "scenario/instance" + number));
        experimentTriples.add(new Triple("experiment/instance" + number, "experiment/start_time", "2001-10-26T21:32:52"));
        experimentTriples.add(new Triple("experiment/instance" + number, "experiment/end_time", "2001-10-26T21:32:52"));
        experimentTriples.add(new Triple("experiment/instance" + number, "experiment/temperature", "12" + number));
        experimentTriples.add(new Triple("experiment/instance" + number, "experiment/environment_note", "envNote" + number));
        experimentTriples.add(new Triple("experiment/instance" + number, "experiment/subject_person", "person/test_subject/instance" + number));
        experimentTriples.add(new Triple("experiment/instance" + number, "experiment/weather", "weather/instance" + number));
        experimentTriples.add(new Triple("experiment/instance" + number, "experiment/owner_person", "person/researcher/instance" + number));
        experimentTriples.add(new Triple("experiment/instance" + number, "experiment/research_group", "research_group/instance" + number));
        experimentTriples.add(new Triple("experiment/instance" + number, "experiment/private", "2" + number));
        experimentTriples.add(new Triple("experiment/instance" + number, "experiment/artefact", "artefact/instance" + number));
        experimentTriples.add(new Triple("experiment/instance" + number, "experiment/subject_group", "subject_group/instance" + number));
        experimentTriples.add(new Triple("experiment/instance" + number, "experiment/electrode_configuration", "electrode_configuration/instance" + number));
        experimentTriples.add(new Triple("experiment/instance" + number, "experiment/digitization", "digitization/instance" + number));
        experimentTriples.add(new Triple("experiment/instance" + number, "experiment/coexperiment_person", "person/researcher/instance" + number));
        experimentTriples.add(new Triple("experiment/instance" + number, "experiment/artefact_artefact_removing_method", "artefact_removing_method/instance" + number));
        experimentTriples.add(new Triple("experiment/instance" + number, "experiment/disease", "disease/instance" + number));
        experimentTriples.add(new Triple("experiment/instance" + number, "experiment/pharmaceutical", "pharmaceutical/instance" + number));
        experimentTriples.add(new Triple("experiment/instance" + number, "experiment/project_type", "project_type/instance" + number));
        experimentTriples.add(new Triple("experiment/instance" + number, "experiment/hardware", "hardware/instance" + number));
        experimentTriples.add(new Triple("experiment/instance" + number, "experiment/software", "software/instance" + number));


        return experimentTriples;
    }

    private List<Triple> createSoftware(int number) {

        List<Triple> softwareTriples = new ArrayList<Triple>();

        softwareTriples.add(new Triple("software/instance" + number, "software/title", "titleSoftware" + number));
        softwareTriples.add(new Triple("software/instance" + number, "software/description", "descriptionSw" + number));
        softwareTriples.add(new Triple("software/instance" + number, "software/is_default", "1" + number));
        softwareTriples.add(new Triple("software/instance" + number, "software/research_group", "research_group/instance" + number));

        return softwareTriples;
    }

    private List<Triple> createHardware(int number) {

        List<Triple> hardwareTriples = new ArrayList<Triple>();

        hardwareTriples.add(new Triple("hardware/instance" + number, "hardware/title", "titleHw" + number));
        hardwareTriples.add(new Triple("hardware/instance" + number, "hardware/type", "type" + number));
        hardwareTriples.add(new Triple("hardware/instance" + number, "hardware/description", "description" + number));
        hardwareTriples.add(new Triple("hardware/instance" + number, "hardware/is_default", "2" + number));
        hardwareTriples.add(new Triple("hardware/instance" + number, "hardware/research_group", "research_group/instance" + number));

        return hardwareTriples;
    }

    private List<Triple> createSubjectGroup(int number) {

        List<Triple> subjGroupTriples = new ArrayList<Triple>();

        subjGroupTriples.add(new Triple("subject_group/instance" + number, "subject_group/title", "title" + number));
        subjGroupTriples.add(new Triple("subject_group/instance" + number, "subject_group/description", "description" + number));

        return subjGroupTriples;
    }

    private List<Triple> createProjectType(int number) {

        List<Triple> projectTypeTriples = new ArrayList<Triple>();

        projectTypeTriples.add(new Triple("project_type/instance" + number, "project_type/title", "title" + number));
        projectTypeTriples.add(new Triple("project_type/instance" + number, "project_type/description", "description" + number));
        projectTypeTriples.add(new Triple("project_type/instance" + number, "project_type/research_group", "research_group/instance" + number));

        return projectTypeTriples;
    }

    private List<Triple> createArtefactRemMeth(int number) {

        List<Triple> projectTypeTriples = new ArrayList<Triple>();

        projectTypeTriples.add(new Triple("artefact_removing_method/instance" + number, "artefact_removing_method/title", "title" + number));
        projectTypeTriples.add(new Triple("artefact_removing_method/instance" + number, "artefact_removing_method/description", "description" + number));
        projectTypeTriples.add(new Triple("artefact_removing_method/instance" + number, "artefact_removing_method/is_default", "1" + number));
        projectTypeTriples.add(new Triple("artefact_removing_method/instance" + number, "artefact_removing_method/research_group", "research_group/instance" + number));

        return projectTypeTriples;
    }

    private List<Triple> createWeather(int number) {

        List<Triple> WeatherTriples = new ArrayList<Triple>();

        WeatherTriples.add(new Triple("weather/instance" + number, "weather/title", "title" + number));
        WeatherTriples.add(new Triple("weather/instance" + number, "weather/description", "description" + number));
        WeatherTriples.add(new Triple("weather/instance" + number, "weather/is_default", "1" + number));
        WeatherTriples.add(new Triple("weather/instance" + number, "weather/research_group", "research_group/instance" + number));

        return WeatherTriples;
    }

    private List<Triple> createDisease(int number) {

        List<Triple> DiseaseTriples = new ArrayList<Triple>();

        DiseaseTriples.add(new Triple("disease/instance" + number, "disease/title", "title" + number));
        DiseaseTriples.add(new Triple("disease/instance" + number, "disease/description", "description" + number));
        DiseaseTriples.add(new Triple("disease/instance" + number, "disease/research_group", "research_group/instance" + number));

        return DiseaseTriples;
    }

    private List<Triple> createArtefact(int number) {

        List<Triple> artefactTriples = new ArrayList<Triple>();

        artefactTriples.add(new Triple("artefact/instance" + number, "artefact/compensation", "compensation" + number));
        artefactTriples.add(new Triple("artefact/instance" + number, "artefact/reject_condition", "rejectCondition" + number));
        artefactTriples.add(new Triple("artefact/instance" + number, "artefact/research_group", "research_group/instance" + number));

        return artefactTriples;
    }

    private List<Triple> createPharmaceutical(int number) {

        List<Triple> pharmaceuticalTriples = new ArrayList<Triple>();

        pharmaceuticalTriples.add(new Triple("pharmaceutical/instance" + number, "pharmaceutical/title", "title" + number));
        pharmaceuticalTriples.add(new Triple("pharmaceutical/instance" + number, "pharmaceutical/description", "description" + number));
        pharmaceuticalTriples.add(new Triple("pharmaceutical/instance" + number, "pharmaceutical/research_group", "research_group/instance" + number));

        return pharmaceuticalTriples;
    }

    private List<Triple> createDigitization(int number) {

        List<Triple> digitizationTriples = new ArrayList<Triple>();

        digitizationTriples.add(new Triple("digitization/instance" + number, "digitization/gain", "5" + number));
        digitizationTriples.add(new Triple("digitization/instance" + number, "digitization/filter", "filters" + number));
        digitizationTriples.add(new Triple("digitization/instance" + number, "digitization/sampling_rate", "8" + number));
        digitizationTriples.add(new Triple("digitization/instance" + number, "digitization/research_group", "research_group/instance" + number));

        return digitizationTriples;
    }

    private List<Triple> createAnalysis(int number) {

        List<Triple> analysisTriples = new ArrayList<Triple>();

        analysisTriples.add(new Triple("analysis/instance" + number, "analysis/epochs_number", "5" + number));
        analysisTriples.add(new Triple("analysis/instance" + number, "analysis/prestimulus_time", "2" + number));
        analysisTriples.add(new Triple("analysis/instance" + number, "analysis/poststimulus_time", "8" + number));
        analysisTriples.add(new Triple("analysis/instance" + number, "analysis/description", "description" + number));
        analysisTriples.add(new Triple("analysis/instance" + number, "analysis/research_group", "research_group/instance" + number));

        return analysisTriples;
    }

    private List<Triple> createDataFile(int number) {

        List<Triple> dataFileTriples = new ArrayList<Triple>();

        dataFileTriples.add(new Triple("data_file/instance" + number, "data_file/experiment", "experiment/instance" + number));
        dataFileTriples.add(new Triple("data_file/instance" + number, "data_file/mimetype", "mimeType" + number));
        dataFileTriples.add(new Triple("data_file/instance" + number, "data_file/filename", "fileName" + number));
        dataFileTriples.add(new Triple("data_file/instance" + number, "data_file/description", "description" + number));
        dataFileTriples.add(new Triple("data_file/instance" + number, "data_file/analysis", "analysis/instance" + number));

        return dataFileTriples;
    }

    private List<Triple> createScenario(int number) {

        List<Triple> scenarioTriples = new ArrayList<Triple>();

        scenarioTriples.add(new Triple("scenario/instance" + number, "scenario/title", "title" + number));
        scenarioTriples.add(new Triple("scenario/instance" + number, "scenario/scenario_length", "225" + number));
        scenarioTriples.add(new Triple("scenario/instance" + number, "scenario/description", "description" + number));
        scenarioTriples.add(new Triple("scenario/instance" + number, "scenario/owner", "person/researcher/instance" + number));
        scenarioTriples.add(new Triple("scenario/instance" + number, "scenario/research_group", "research_group/instance" + number));
        scenarioTriples.add(new Triple("scenario/instance" + number, "scenario/private", "1" + number));
        scenarioTriples.add(new Triple("scenario/instance" + number, "scenario/scenario_name", "scenarioName" + number));
        scenarioTriples.add(new Triple("scenario/instance" + number, "scenario/mimetype", "mimeType" + number));

        return scenarioTriples;
    }

    private List<Triple> createElectrodeConfiguration(int number) {

        List<Triple> elConfigTriples = new ArrayList<Triple>();

        elConfigTriples.add(new Triple("electrode_configuration/instance" + number, "electrode_configuration/impedance", "147" + number));
        elConfigTriples.add(new Triple("electrode_configuration/instance" + number, "electrode_configuration/electrode_system", "electrode_system/instance" + number));
        elConfigTriples.add(new Triple("electrode_configuration/instance" + number, "electrode_configuration/descript_data_file", "data_file/instance" + number));
        elConfigTriples.add(new Triple("electrode_configuration/instance" + number, "electrode_configuration/electrode_location", "electrode_location/instance" + number));

        return elConfigTriples;
    }

    private List<Triple> createElectrodeSystem(int number) {

        List<Triple> elSystemTriples = new ArrayList<Triple>();

        elSystemTriples.add(new Triple("electrode_system/instance" + number, "electrode_system/title", "title" + number));
        elSystemTriples.add(new Triple("electrode_system/instance" + number, "electrode_system/description", "description" + number));
        elSystemTriples.add(new Triple("electrode_system/instance" + number, "electrode_system/is_default", "0" + number));
        elSystemTriples.add(new Triple("electrode_system/instance" + number, "electrode_system/research_group", "research_group/instance" + number));

        return elSystemTriples;
    }

    private List<Triple> createElectrodeLocation(int number) {

        List<Triple> elLocationTriples = new ArrayList<Triple>();

        elLocationTriples.add(new Triple("electrode_location/instance" + number, "electrode_location/title", "title" + number));
        elLocationTriples.add(new Triple("electrode_location/instance" + number, "electrode_location/shortcut", "shortcut" + number));
        elLocationTriples.add(new Triple("electrode_location/instance" + number, "electrode_location/description", "description" + number));
        elLocationTriples.add(new Triple("electrode_location/instance" + number, "electrode_location/is_deafult", "1" + number));
        elLocationTriples.add(new Triple("electrode_location/instance" + number, "electrode_location/electrode_fix", "electrode_fix/instance" + number));
        elLocationTriples.add(new Triple("electrode_location/instance" + number, "electrode_location/electrode_type", "electrode_type/instance" + number));
        elLocationTriples.add(new Triple("electrode_location/instance" + number, "electrode_location/research_group", "research_group/instance" + number));

        return elLocationTriples;
    }

    private List<Triple> createElectrodeType(int number) {

        List<Triple> elTypeTriples = new ArrayList<Triple>();

        elTypeTriples.add(new Triple("electrode_type/instance" + number, "electrode_type/title", "title" + number));
        elTypeTriples.add(new Triple("electrode_type/instance" + number, "electrode_type/description", "description" + number));
        elTypeTriples.add(new Triple("electrode_type/instance" + number, "electrode_type/is_default", "0" + number));
        elTypeTriples.add(new Triple("electrode_type/instance" + number, "electrode_type/research_group", "research_group/instance" + number));

        return elTypeTriples;
    }

    private List<Triple> createElectrodeFix(int number) {

        List<Triple> elFixTriples = new ArrayList<Triple>();

        elFixTriples.add(new Triple("electrode_fix/instance" + number, "electrode_fix/title", "title" + number));
        elFixTriples.add(new Triple("electrode_fix/instance" + number, "electrode_fix/description", "description" + number));
        elFixTriples.add(new Triple("electrode_fix/instance" + number, "electrode_fix/is_default", "0" + number));
        elFixTriples.add(new Triple("electrode_fix/instance" + number, "electrode_fix/research_group", "research_group/instance" + number));

        return elFixTriples;
    }

    private List<Triple> createStimulus(int number) {

        List<Triple> stimulusTriples = new ArrayList<Triple>();

        stimulusTriples.add(new Triple("stimulus/instance" + number, "stimulus/description", "description" + number));

        stimulusTriples.add(new Triple("stimulus_type/instance" + number, "stimulus_type/description", "description" + number));
        stimulusTriples.add(new Triple("stimulus_type/instance" + number, "stimulus_type/is_default", "0" + number));
        stimulusTriples.add(new Triple("stimulus_type/instance" + number, "stimulus_type/research_group", "research_group/instance" + number));

        stimulusTriples.add(new Triple("scenario_stimulus/instance" + number, "scenario_stimulus/stimulus", "stimulus/instance" + number));
        stimulusTriples.add(new Triple("scenario_stimulus/instance" + number, "scenario_stimulus/stimulus_type", "stimulus_type/instance" + number));

        return stimulusTriples;
    }
}
