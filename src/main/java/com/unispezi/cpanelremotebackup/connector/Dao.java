package com.unispezi.cpanelremotebackup.connector;

/**
 * Encapsulates access to remote data
 */
public class Dao {

    private CPanelConnector cPanel;

    public Dao() {
        cPanel = new CPanelConnector();
    }

    /**
     * Asks the remote system to start a backup. Will not block
     * until backup is complete
     */
    public void triggerBackupAsync(){
        cPanel.triggerBakupAsync();
    }

}
