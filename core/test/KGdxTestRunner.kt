/*******************************************************************************
 * Copyright 2015 See AUTHORS file.
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
 ******************************************************************************/

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.backends.headless.HeadlessApplication
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration
import com.badlogic.gdx.utils.Timer
import org.junit.runner.notification.RunNotifier
import org.junit.runners.BlockJUnit4ClassRunner
import org.junit.runners.model.FrameworkMethod

//based on https://github.com/TomGrill/gdx-testing/blob/master/tests/src/de/tomgrill/gdxtesting/GdxTestRunner.java
class KGdxTestRunner(klass : Class<*>) : BlockJUnit4ClassRunner(klass), ApplicationListener  {

	init {
		val conf = HeadlessApplicationConfiguration()
		HeadlessApplication(this, conf)
	}

	val invokeInRender : MutableMap<FrameworkMethod, RunNotifier> = HashMap()

	override fun create() { }

	override fun resume() { }

	override fun render() {
		synchronized (invokeInRender) {
			invokeInRender.entries.forEach { entry ->
				super.runChild(entry.key, entry.value)
			}
			invokeInRender.clear();
		}
	}

	override fun resize(width: Int, height: Int) { }

	override fun pause() { }

	override fun dispose() { }

	override fun runChild(method : FrameworkMethod, notifier : RunNotifier) {
		synchronized (invokeInRender) {
			// add for invoking in render phase, where gl context is available
			invokeInRender.put(method, notifier);
		}
		// wait until that test was invoked
		waitUntilInvokedInRenderMethod();
	}

	private fun waitUntilInvokedInRenderMethod() {
		var invokeIsEmpty = false

		try {
			while (!invokeIsEmpty) {
				Thread.sleep(10)
				synchronized (invokeInRender) {
					if (invokeInRender.isEmpty()) invokeIsEmpty = true
				}
			}
		} catch (e : InterruptedException) {
			e.printStackTrace();
		}
	}
}
