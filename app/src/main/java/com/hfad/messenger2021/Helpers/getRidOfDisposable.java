package com.hfad.messenger2021.Helpers;

import android.util.Log;

import io.reactivex.rxjava3.disposables.Disposable;

public class getRidOfDisposable {
    public static void getRid(Disposable toDispose){
        if(toDispose != null && !toDispose.isDisposed()){
            toDispose.dispose();
        }
    }
}
