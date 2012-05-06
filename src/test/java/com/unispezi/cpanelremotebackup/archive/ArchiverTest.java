package com.unispezi.cpanelremotebackup.archive;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * Contains all logic that deals with archives (tar / gz)
 */
public class ArchiverTest {
    Archiver archiver;
    File correctTarGz = new File(ArchiverTest.class.getResource("/com/unispezi/cpanelremotebackup/archive/test.tar.gz").getFile());
    File corruptTarGz = new File(ArchiverTest.class.getResource("/com/unispezi/cpanelremotebackup/archive/corrupted.tar.gz").getFile());
    File oneBitCorruptedOnlyTarGz = new File(ArchiverTest.class.getResource("/com/unispezi/cpanelremotebackup/archive/oneBitCorruptedOnly.tar.gz").getFile());
    File truncatedTarGz = new File(ArchiverTest.class.getResource("/com/unispezi/cpanelremotebackup/archive/truncated.tar.gz").getFile());

    @Before
    public void setUp(){
        archiver = new Archiver(10000);
    }

    @Test
    public void onCorrectTgzReturnsTrue(){
        Assert.assertTrue(archiver.verifyDownloadedBackup(correctTarGz));
    }

    @Test
    public void onTruncatedTgzReturnsFalse(){
        Assert.assertFalse(archiver.verifyDownloadedBackup(truncatedTarGz));
    }

    @Test
    public void onCorruptedTgzReturnsFalse(){
        Assert.assertFalse(archiver.verifyDownloadedBackup(corruptTarGz));
    }

    @Test
    public void onCorruptedOneBitOnlyTgzReturnsFalse(){
        Assert.assertFalse(archiver.verifyDownloadedBackup(oneBitCorruptedOnlyTarGz));
    }

}
