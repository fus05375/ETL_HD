package mainApp;

import dbUtil.LoginModel;
import dbUtil.dbConnection;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;

public class MainAppCtrl implements Initializable {

    LoginModel loginModel = new LoginModel();

    @FXML
    public Label dbstatus;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (this.loginModel.isDatabaseConnected()) {
            this.dbstatus.setText("Connected to Database");
            this.dbstatus.setTextFill(Color.web("#00FF00"));
        } else {
            this.dbstatus.setText("Not connected to Database");
            this.dbstatus.setTextFill(Color.web("#FF0000"));
        }
    }

    @FXML
    private TextField keywordSearch;
    @FXML
    private TextField titleSearch;

    @FXML
    private TableView<JobAdvertisement> jobAdvertisementTableView;
    @FXML
    private TableColumn<JobAdvertisement, String> idColumn;
    @FXML
    private TableColumn<JobAdvertisement, String> jobKeywordColumn;
    @FXML
    private TableColumn<JobAdvertisement, String> titleColumn;
    @FXML
    private TableColumn<JobAdvertisement, String> workAddressColumn;
    @FXML
    private TableColumn<JobAdvertisement, String> announcementDateColumn;
    @FXML
    private TableColumn<JobAdvertisement, String> contractTypeColumn;
    @FXML
    private TableColumn<JobAdvertisement, String> specialRequirementsColumn;
    @FXML
    private TableColumn<JobAdvertisement, String> salaryColumn;

    @FXML
    public TextArea textAreaPanel;

    private ObservableList<JobAdvertisement> data;
    private ObservableList<JobAdvertisement> tempData;
    private String sqlGetAllDataSql = "SELECT * FROM JobAdvertisement ";

    public void getAllAdvsFromDb() {
        try {
            Connection connection = dbConnection.getConnection();
            this.data = FXCollections.observableArrayList();
            ResultSet rs = connection.createStatement().executeQuery(sqlGetAllDataSql);
            while (rs.next()) {
                this.data.add(new JobAdvertisement(
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getString(8)));
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
    }


    public void loadJobAdvertisement(ActionEvent event) {
        getAllAdvsFromDb();
        this.idColumn.setCellValueFactory(new PropertyValueFactory<JobAdvertisement, String>("id"));
        this.jobKeywordColumn.setCellValueFactory(new PropertyValueFactory<JobAdvertisement, String>("jobKeyword"));
        this.titleColumn.setCellValueFactory(new PropertyValueFactory<JobAdvertisement, String>("title"));
        this.workAddressColumn.setCellValueFactory(new PropertyValueFactory<JobAdvertisement, String>("workAddress"));
        this.announcementDateColumn.setCellValueFactory(new PropertyValueFactory<JobAdvertisement, String>("announcementDate"));
        this.contractTypeColumn.setCellValueFactory(new PropertyValueFactory<JobAdvertisement, String>("contractType"));
        this.specialRequirementsColumn.setCellValueFactory(new PropertyValueFactory<JobAdvertisement, String>("specialRequirements"));
        this.salaryColumn.setCellValueFactory(new PropertyValueFactory<JobAdvertisement, String>("salary"));

        this.jobAdvertisementTableView.setItems(null);
        this.jobAdvertisementTableView.setItems(this.data);

    }

    public void filterJobAdvertisementsByTitle(ActionEvent event) {
        try {
            String keywordSearchString = this.titleSearch.getText();

            String filterJobAdvsByTitle = "SELECT * FROM JobAdvertisement WHERE title LIKE ?";

            Connection connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(filterJobAdvsByTitle);
            statement.setString(1, "%" + keywordSearchString + "%");

            this.tempData = FXCollections.observableArrayList();
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                this.tempData.add(new JobAdvertisement(
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getString(8)));
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
        this.idColumn.setCellValueFactory(new PropertyValueFactory<JobAdvertisement, String>("id"));
        this.jobKeywordColumn.setCellValueFactory(new PropertyValueFactory<JobAdvertisement, String>("jobKeyword"));
        this.titleColumn.setCellValueFactory(new PropertyValueFactory<JobAdvertisement, String>("title"));
        this.workAddressColumn.setCellValueFactory(new PropertyValueFactory<JobAdvertisement, String>("workAddress"));
        this.announcementDateColumn.setCellValueFactory(new PropertyValueFactory<JobAdvertisement, String>("announcementDate"));
        this.contractTypeColumn.setCellValueFactory(new PropertyValueFactory<JobAdvertisement, String>("contractType"));
        this.specialRequirementsColumn.setCellValueFactory(new PropertyValueFactory<JobAdvertisement, String>("specialRequirements"));
        this.salaryColumn.setCellValueFactory(new PropertyValueFactory<JobAdvertisement, String>("salary"));

        this.jobAdvertisementTableView.setItems(null);
        this.jobAdvertisementTableView.setItems(this.tempData);

    }

    public void upsertJobAdvertisements(ActionEvent event) throws SQLException {
        String sqlInsert = "INSERT INTO JobAdvertisement(id, jobKeyword, title, workAddress, announcementDate, contractType, specialRequirements, salary) VALUES (?,?,?,?,?,?,?,?)";
        String sqlUpdate = "UPDATE JobAdvertisement SET jobKeyword=?, title=?, workAddress=?, announcementDate=?, contractType=?, specialRequirements=?, salary=? WHERE id=? ";

        List<JobAdvertisement> tempJobAdvertisementList = this.tempData;

        String keywordSearchString = this.keywordSearch.getText();
        if (!keywordSearchString.isEmpty()) {
            Connection connection = dbConnection.getConnection();

            if (tempJobAdvertisementList != null || !tempJobAdvertisementList.isEmpty())


                getAllAdvsFromDb();
            int updatedRecords = 0;

            for (JobAdvertisement jobAdvertisementDB : this.data) {
                for (JobAdvertisement jobAdvertisement : tempJobAdvertisementList) {
                    if (jobAdvertisementDB.getId().equals(jobAdvertisement.getId())) {
                        System.out.println("foundMatch");

                        try {
                            PreparedStatement statement = connection.prepareStatement(sqlUpdate);
                            statement.setString(1, jobAdvertisement.getJobKeyword());
                            statement.setString(2, jobAdvertisement.getTitle());
                            statement.setString(3, jobAdvertisement.getWorkAddress());
                            statement.setString(4, jobAdvertisement.getAnnouncementDate());
                            statement.setString(5, jobAdvertisement.getContractType());
                            statement.setString(6, jobAdvertisement.getSpecialRequirements());
                            statement.setString(7, jobAdvertisement.getSalary());
                            statement.setString(8, jobAdvertisement.getId());
                            statement.execute();
                        } catch (SQLException e) {
                            System.out.println("Error: " + e);
                        }
                        tempJobAdvertisementList.remove(jobAdvertisement);
                        updatedRecords++;
                        break;
                    }
                }

            }
            try {
                PreparedStatement statement = connection.prepareStatement(sqlInsert);
                for (JobAdvertisement jobAdvertisement : tempJobAdvertisementList) {
                    statement.setString(1, jobAdvertisement.getId());
                    statement.setString(2, jobAdvertisement.getJobKeyword());
                    statement.setString(3, jobAdvertisement.getTitle());
                    statement.setString(4, jobAdvertisement.getWorkAddress());
                    statement.setString(5, jobAdvertisement.getAnnouncementDate());
                    statement.setString(6, jobAdvertisement.getContractType());
                    statement.setString(7, jobAdvertisement.getSpecialRequirements());
                    statement.setString(8, jobAdvertisement.getSalary());
                    statement.execute();
                }
                connection.close();
                loadJobAdvertisement(event);
                this.textAreaPanel.appendText("Updated records: " + updatedRecords + "\n");
                this.textAreaPanel.appendText("New records: " + tempJobAdvertisementList.size() + "\n");
            } catch (SQLException e) {
                System.out.println("Error: " + e);
            }
        }


    }

    public void clearJobAdvertisementsDb(ActionEvent event) throws SQLException {
        String sqlCleanDb = "DELETE FROM JobAdvertisement";

        Connection connection = dbConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sqlCleanDb);
        try {
            statement.execute();
            connection.close();
            loadJobAdvertisement(event);
            this.textAreaPanel.appendText("database wiped\n");
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
    }

    public void onAdvSelected(MouseEvent event) throws SQLException {
        if (event.getClickCount() == 1) //Checking double click
        {
            TableView.TableViewSelectionModel<JobAdvertisement> jobAdvertisement = jobAdvertisementTableView.getSelectionModel();
            this.textAreaPanel.appendText("Selected adv title: " + jobAdvertisement.getSelectedItem().getTitle() + "\n");
            this.textAreaPanel.appendText("Selected adv address: " + jobAdvertisement.getSelectedItem().getWorkAddress() + "\n");
            this.textAreaPanel.appendText("Selected adv time bound job: " + jobAdvertisement.getSelectedItem().getContractType() + "\n");
            this.textAreaPanel.appendText("Selected adv contract: " + jobAdvertisement.getSelectedItem().getSpecialRequirements() + "\n");
            this.textAreaPanel.appendText("Selected adv salary: " + jobAdvertisement.getSelectedItem().getSalary() + "\n");
            this.textAreaPanel.appendText("~\n");

        }
    }

    private String keyword;
    private List<JobAdvertisement> scrappedJobAdvertisementList;
    private TxtFile saveToTXT;
    private UrlResponse urlResponse;
    private final String FILE_NAME = "OLX_HTML";
    @FXML
    private Button extractBtn;
    @FXML
    private Button transformBtn;
    @FXML
    private Button loadBtn;

    public void etlController(ActionEvent event) throws SQLException {
        extractController(event);
        transformController(event);
        loadController(event);
    }

    public void extractController(ActionEvent event) {
        this.keyword = this.keywordSearch.getText();
        if (keyword.isEmpty()) {
            this.textAreaPanel.appendText("No search key provided\n");
        } else {
            this.textAreaPanel.appendText("Scrapping data\n");
            this.urlResponse = new UrlResponse(keyword);
            this.saveToTXT = new TxtFile(FILE_NAME, urlResponse.getContentBuilder().toString());
            this.saveToTXT.createFile();
            this.saveToTXT.writeToFile();
            this.textAreaPanel.appendText("Plain HTML: " + "C:/OLX/" + FILE_NAME + ".txt" + "\n");
        }
        this.transformBtn.setDisable(false);
    }

    public void transformController(ActionEvent event) {
        this.scrappedJobAdvertisementList = Transform.run(this.keyword, FILE_NAME);
        this.textAreaPanel.appendText("Transformed " + scrappedJobAdvertisementList.size() + " advs\n");
        this.loadBtn.setDisable(false);
    }

    public void loadController(ActionEvent event) throws SQLException {
        this.tempData = FXCollections.observableArrayList();

        for (JobAdvertisement jobAdvertisement : this.scrappedJobAdvertisementList) {
            this.tempData.add(new JobAdvertisement(
                            jobAdvertisement.getJobKeyword().toString(),
                            jobAdvertisement.getId().toString(),
                            jobAdvertisement.getTitle().toString(),
                            jobAdvertisement.getWorkAddress().toString(),
                            jobAdvertisement.getAnnouncementDate().toString(),
                            jobAdvertisement.getContractType().toString(),
                            jobAdvertisement.getSpecialRequirements().toString(),
                            jobAdvertisement.getSalary().toString()
                    )
            );
        }

        this.saveToTXT.removeFile();
        this.textAreaPanel.appendText("Removed plain " + FILE_NAME + ".txt file\n");
        upsertJobAdvertisements(event);
        this.transformBtn.setDisable(true);
        this.loadBtn.setDisable(true);
    }


    public void exportDbToCsv(ActionEvent event) throws SQLException, IOException {
        try {
            Connection connection = dbConnection.getConnection();
            ResultSet results = connection.createStatement().executeQuery(sqlGetAllDataSql);
            File filePath = new File("c:/OLX");
            String fileName = filePath.toString() + "\\dbExport.csv";
            // Open CSV file.
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName));

            // Add table headers to CSV file.
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                    .withHeader(results.getMetaData()).withQuoteMode(QuoteMode.ALL));

            // Add data rows to CSV file.
            while (results.next()) {

                csvPrinter.printRecord(
                        results.getString(1),
                        results.getString(2),
                        results.getString(3),
                        results.getString(4),
                        results.getString(5),
                        results.getString(6),
                        results.getString(7),
                        results.getString(8));

            }

            // Close CSV file.
            csvPrinter.flush();
            csvPrinter.close();
            this.textAreaPanel.appendText("database exported to csv, directory: " + (Paths.get(fileName) + "\n"));

        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
    }

    public void exportDbRecordsToTxtFiles(ActionEvent event) throws SQLException, IOException {
        try {
            Connection connection = dbConnection.getConnection();
            ResultSet results = connection.createStatement().executeQuery(sqlGetAllDataSql);

            List<JobAdvertisement> allJobAdvs = new ArrayList<>();
            while (results.next()) {
                allJobAdvs.add(new JobAdvertisement(
                        results.getString(1),
                        results.getString(2),
                        results.getString(3),
                        results.getString(4),
                        results.getString(5),
                        results.getString(6),
                        results.getString(7),
                        results.getString(8)));
            }
            for (JobAdvertisement jobAdvertisement : allJobAdvs) {
                StringBuilder contentBuilder = new StringBuilder();
                contentBuilder.append("Id: " + jobAdvertisement.getId() + "\n");
                contentBuilder.append("Keyword: " + jobAdvertisement.getJobKeyword() + "\n");
                contentBuilder.append("Title: " + jobAdvertisement.getTitle() + "\n");
                contentBuilder.append("Work Address: " + jobAdvertisement.getWorkAddress() + "\n");
                contentBuilder.append("Announcement Date: " + jobAdvertisement.getAnnouncementDate() + "\n");
                contentBuilder.append("Contract Type: " + jobAdvertisement.getContractType() + "\n");
                contentBuilder.append("Special Requirements: " + jobAdvertisement.getSpecialRequirements() + "\n");
                contentBuilder.append("Salary: " + jobAdvertisement.getSalary() + "\n");

                TxtFile recordTxt = new TxtFile(jobAdvertisement.getId(), contentBuilder.toString());
                recordTxt.createFile();
                recordTxt.writeToFile();
            }


            this.textAreaPanel.appendText("Database records exported to txt files, directory: C:/OLX/\n");

        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
    }

}
