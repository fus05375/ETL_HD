package mainApp;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class JobAdvertisement {

    private StringProperty jobKeyword;
    private StringProperty id;
    private StringProperty title;
    private StringProperty workAddress;
    private StringProperty announcementDate;
    private StringProperty contractType;
    private StringProperty specialRequirements;
    private StringProperty salary;

    public JobAdvertisement(String jobKeyword, String id, String title, String workAddress, String announcementDate, String contractType, String specialRequirements, String salary) {
        this.jobKeyword = new SimpleStringProperty(jobKeyword);
        this.id = new SimpleStringProperty(id);
        this.title = new SimpleStringProperty(title);
        this.workAddress = new SimpleStringProperty(workAddress);
        this.announcementDate = new SimpleStringProperty(announcementDate);
        this.contractType = new SimpleStringProperty(contractType);
        this.specialRequirements = new SimpleStringProperty(specialRequirements);
        this.salary = new SimpleStringProperty(salary);
    }

    public String getJobKeyword() {
        return jobKeyword.get();
    }

    public StringProperty jobKeywordProperty() {
        return jobKeyword;
    }

    public void setJobKeyword(String jobKeyword) {
        this.jobKeyword.set(jobKeyword);
    }

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getWorkAddress() {
        return workAddress.get();
    }

    public StringProperty workAddressProperty() {
        return workAddress;
    }

    public void setWorkAddress(String workAddress) {
        this.workAddress.set(workAddress);
    }

    public String getAnnouncementDate() {
        return announcementDate.get();
    }

    public StringProperty announcementDateProperty() {
        return announcementDate;
    }

    public void setAnnouncementDate(String announcementDate) {
        this.announcementDate.set(announcementDate);
    }

    public String getContractType() {
        return contractType.get();
    }

    public StringProperty contractTypeProperty() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType.set(contractType);
    }

    public String getSpecialRequirements() {
        return specialRequirements.get();
    }

    public StringProperty specialRequirementsProperty() {
        return specialRequirements;
    }

    public void setSpecialRequirements(String specialRequirements) {
        this.specialRequirements.set(specialRequirements);
    }

    public String getSalary() {
        return salary.get();
    }

    public StringProperty salaryProperty() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary.set(salary);
    }

}
