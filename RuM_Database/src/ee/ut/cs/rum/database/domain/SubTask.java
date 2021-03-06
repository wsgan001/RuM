package ee.ut.cs.rum.database.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import ee.ut.cs.rum.database.domain.enums.SubTaskStatus;
import ee.ut.cs.rum.database.domain.interfaces.RumUpdatableEntity;

@Entity
@Table(name="sub_task")
public class SubTask implements RumUpdatableEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "name")
	private String name;
	@Column(name = "description")
	private String description;
	@Column(name = "status")
	private SubTaskStatus status;
	@JoinColumn(name = "plugin_fk")
	private Plugin plugin;
	@Column(name = "configuration_values", columnDefinition = "TEXT")
	private String configurationValues;
	
	@OneToMany(mappedBy="requiredBySubTask", cascade=CascadeType.PERSIST)
	private List<SubTaskDependency> requiredDependencies;
	@OneToMany(mappedBy="fulfilledBySubTask", cascade=CascadeType.PERSIST)
	private List<SubTaskDependency> fulfilledDependencies;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "task_fk")
	private Task task;
	@Column(name = "output_path")
	private String outputPath;
	@JoinColumn(name = "created_by_fk")
	private UserAccount createdBy;
	@Column(name = "created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	@JoinColumn(name = "last_modified_by_fk")
	private UserAccount lastModifiedBy;
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
	public SubTaskStatus getStatus() {
		return status;
	}
	public void setStatus(SubTaskStatus status) {
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
	
	public List<SubTaskDependency> getRequiredDependencies() {
		return requiredDependencies;
	}
	public void setRequiredDependencies(List<SubTaskDependency> requiredDependencies) {
		for (SubTaskDependency subTaskDependency : requiredDependencies) {
			subTaskDependency.setRequiredBySubTask(this);
		}
		this.requiredDependencies = requiredDependencies;
	}
	public List<SubTaskDependency> getFulfilledDependencies() {
		return fulfilledDependencies;
	}
	public void setFulfilledDependencies(List<SubTaskDependency> fulfilledDependencies) {
		for (SubTaskDependency subTaskDependency : fulfilledDependencies) {
			subTaskDependency.setFulfilledBySubTask(this);
		}
		this.fulfilledDependencies = fulfilledDependencies;
	}
	public Task getTask() {
		return task;
	}
	public void setTask(Task task) {
		this.task = task;
	}
	public String getOutputPath() {
		return outputPath;
	}
	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}
	public UserAccount getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(UserAccount createdBy) {
		this.createdBy = createdBy;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public UserAccount getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(UserAccount lastModifiedBy) {
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
		return "SubTask [id=" + id + ", name=" + name + ", description=" + description + ", status=" + status
				+ ", plugin=" + plugin + ", configurationValues=" + configurationValues + ", task=" + task
				+ ", outputPath=" + outputPath + ", createdBy=" + createdBy + ", createdAt=" + createdAt
				+ ", lastModifiedBy=" + lastModifiedBy + ", lastModifiedAt=" + lastModifiedAt + "]";
	}
}
