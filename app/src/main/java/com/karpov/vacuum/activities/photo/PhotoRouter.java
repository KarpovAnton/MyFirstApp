package com.karpov.vacuum.activities.photo;

import javax.inject.Inject;

public class PhotoRouter implements PhotoContract.Router {
    @Inject
    PhotoActivity activity;

    @Inject
    public PhotoRouter() {

    }
}
