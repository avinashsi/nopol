/*
 * Copyright (C) 2013 INRIA
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package fr.inria.lille.jefix.synth.precondition;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;

import com.google.common.base.Predicate;

/**
 * @author Favio D. DeMarco
 * 
 */
public enum SpoonStatementPredicate implements Predicate<CtCodeElement> {

	INSTANCE;

	@Override
	public boolean apply(final CtCodeElement input) {
		return input instanceof CtStatement
				&& !(input instanceof CtClass || input instanceof CtBlock
						|| input instanceof CtReturn || input instanceof CtLocalVariable)
						&& input.getParent() instanceof CtStatement;
	}
}