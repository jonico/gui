/*
 * Copyright 2009 CollabNet, Inc. ("CollabNet")
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **/

package com.collabnet.ccf.qc.schemageneration;

import org.eclipse.jface.preference.IPreferenceStore;

import com.collabnet.ccf.qc.Activator;
import com.jacob.com.ComThread;


/**
 * This class is responsible to manage the COM
 * initialization and tear down
 * This class is shared by both the QCReader and the QCWriter
 * @author jnicolai
 *
 */
public class ComHandle {
	private static boolean comInitialized=false;
	private static IPreferenceStore store = Activator.getDefault().getPreferenceStore();
	
	public static void initCOM() {
		if (!comInitialized) {
			ComThread.InitMTA();
			comInitialized=true;
		}
	}

	public static void tearDownCOM(){
		if (comInitialized) {
			if (!store.getBoolean(Activator.PREFERENCES_ADVANCED_PROJECT_MAPPING)) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// Ignore.
				}
			}
			ComThread.Release();
			comInitialized=false;
		}
	}
}