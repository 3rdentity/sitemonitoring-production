package net.sf.sitemonitoring.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;
import net.sf.sitemonitoring.entity.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UpgradeService {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private ConfigurationService configurationService;

	/**
	 * In this method will be upgrades to existing database
	 * 
	 * @throws SQLException
	 */
	public void upgradeDatabase(Configuration configuration) {
		if (configuration.getMonitoringVersion() == null || configuration.getMonitoringVersion().isEmpty()) {
			configuration.setMonitoringVersion("2.1");
			configuration.setInfoMessage("Please don't monitor my websites (like javavids.com and sitemonitoring.sourceforge.net). Lot's of people started doing it and effectively DDOSed them. If you monitor them anyway, your IP address will be blocked!");
			configurationService.saveExcludingPassword(configuration);
			update("update monit_check set condition_value = condition");
		}
		if (configuration.getMonitoringVersion().equals("2.1")) {
			configuration.setDefaultSpiderCheckInterval(60);
			configuration.setDefaultSendEmails(false);
			configuration.setInfoMessage("Please don't monitor my websites (like javavids.com and sitemonitoring.sourceforge.net). Lot's of people started doing it and effectively DDOSed them. If you monitor them anyway, your IP address will be blocked!");
			configuration.setMonitoringVersion("2.1.2");
			configurationService.saveExcludingPassword(configuration);
		}
	}

	private void update(String sql) {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			Statement statement = connection.createStatement();
			statement.executeUpdate(sql);
			statement.close();
		} catch (SQLException ex) {
			log.error("error upgrading to new version", ex);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException ex) {
					log.error("error upgrading to new version", ex);
				}
			}
		}
	}
}
