package com.forumdeitroll.test.servlets;

import com.forumdeitroll.servlets.Action;

public interface SetupStep {

    SetupStep setAttribute(String attributeName, Object attribute);

    SetupStep setParameter(String parameterName, String parameter);

	SetupStep setSessionAttribute(String attributeName, Object attribute);

    CheckStep executeInit() throws Exception;

    CheckStep executeDoBefore();

    CheckStep executeAction(String actionName, Action.Method actionMethod) throws Exception;

}
