package ee.ut.cs.rum.database.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import ee.ut.cs.rum.database.domain.enums.TaskStatus;

@Entity
@Table(name="task")
public class Task {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "name")
	private String name;
	@Column(name = "description")
	private String description;
	@Column(name = "status")
	private TaskStatus status;
	@JoinColumn(name = "plugin_fk")
	private Plugin plugin;
	@Column(name = "configuration_values", columnDefinition = "TEXT")
	private String configurationValues;
	@JoinColumn(name = "project_fk")
	private Project project;
	@Column(name = "output_path")
	private String outputPath;
	@Column(name = "created_by")
	private String createdBy;
	@Column(name = "created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	@Column(name = "last_modified_by")
	private String lastModifiedBy;
	@Column(name = "last_modified_at") //TODO: Implement modifying functionality 
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModifiedAt;
	
	public Long getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public TaskStatus getStatus() {
		return status;
	}
	public void setStatus(TaskStatus status) {
		this.status = status;
	}
	public Plugin getPlugin() {
		return plugin;
	}
	public void setPlugin(Plugin plugin) {
		this.plugin = plugin;
	}
	public String getConfigurationValues() {
		return configurationValues;
	}
	public void setConfigurationValues(String configurationValues) {
		this.configurationValues = configurationValues;
	}
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	public String getOutputPath() {
		return outputPath;
	}
	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	public Date getLastModifiedAt() {
		return lastModifiedAt;
	}
	public void setLastModifiedAt(Date lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}
	
	@Override
	public String toString() {
		return "Task [id=" + id + ", name=" + name + ", description=" + description + ", status=" + status + ", plugin="
				+ plugin + ", configurationValues=" + configurationValues + ", project=" + project + ", outputPath="
				+ outputPath + ", createdBy=" + createdBy + ", createdAt=" + createdAt + ", lastModifiedBy="
				+ lastModifiedBy + ", lastModifiedAt=" + lastModifiedAt + "]";
	}
}
