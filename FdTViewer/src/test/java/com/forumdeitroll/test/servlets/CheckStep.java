package com.forumdeitroll.test.servlets;

import java.util.List;

public interface CheckStep {

    CheckStep checkWebsiteTitle();

    CheckStep checkWebsiteTitleStartsWith(String webSiteTitle);

    CheckStep checkNavigationMessage(String messageContent);

    CheckStep checkLocationHeader(String location);

    CheckStep checkListSize(List<?> list, int size);

    CheckStep checkNoServletException();

}
