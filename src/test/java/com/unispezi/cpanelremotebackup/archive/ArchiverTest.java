/*  Copyright (C) 2012 Carsten Lergenmüller

    Permission is hereby granted, free of charge, to any person obtaining a copy of this
    software and associated documentation files (the "Software"), to deal in the Software
    without restriction, including without limitation the rights to use, copy, modify,
    merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
    permit persons to whom the Software is furnished to do so, subject to the following
    conditions:

        The above copyright notice and this permission notice shall be included in all
        copies or substantial portions of the Software.

        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
        INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
        PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
        HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
        OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
        SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

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
