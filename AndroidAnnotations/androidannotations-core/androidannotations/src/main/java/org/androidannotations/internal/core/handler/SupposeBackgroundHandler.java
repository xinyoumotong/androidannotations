/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.internal.core.handler;

import static com.helger.jcodemodel.JExpr.lit;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.annotations.SupposeBackground;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.holder.EComponentHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;

public class SupposeBackgroundHandler extends SupposeThreadHandler {

	private static final String METHOD_CHECK_BG_THREAD = "checkBgThread";

	public SupposeBackgroundHandler(AndroidAnnotationsEnvironment environment) {
		super(SupposeBackground.class, environment);
	}

	@Override
	public void process(Element element, EComponentHolder holder) throws Exception {
		ExecutableElement executableElement = (ExecutableElement) element;

		JMethod delegatingMethod = codeModelHelper.overrideAnnotatedMethod(executableElement, holder);

		AbstractJClass bgExecutor = getJClass(BackgroundExecutor.class);

		SupposeBackground annotation = element.getAnnotation(SupposeBackground.class);
		String[] serial = annotation.serial();
		JInvocation invocation = bgExecutor.staticInvoke(METHOD_CHECK_BG_THREAD);
		for (String s : serial) {
			invocation.arg(lit(s));
		}

		JBlock body = delegatingMethod.body();
		body.pos(0);
		body.add(invocation);
		body.pos(body.getContents().size());
	}
}
