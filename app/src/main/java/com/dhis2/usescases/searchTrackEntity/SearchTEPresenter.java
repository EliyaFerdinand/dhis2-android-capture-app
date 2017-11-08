package com.dhis2.usescases.searchTrackEntity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import org.hisp.dhis.android.core.option.OptionModel;
import org.hisp.dhis.android.core.program.ProgramModel;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by ppajuelo on 02/11/2017.
 */

public class SearchTEPresenter implements SearchTEContractsModule.Presenter {

    private SearchTEContractsModule.View view;
    @Inject
    SearchTEContractsModule.Interactor interactor;

    private LocationManager locationManager;

    @Inject
    SearchTEPresenter() {
    }

    @Override
    public void init(SearchTEContractsModule.View view) {
        this.view = view;
        interactor.init(view);
        locationManager = (LocationManager) view.getAbstracContext().getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onDateClick(@Nullable DatePickerDialog.OnDateSetListener listener) {
        view.showDateDialog(listener);
    }

    @Override
    public Observable<List<OptionModel>> getOptions(String optionSetId) {
        return interactor.getOptions(optionSetId);
    }

    @Override
    public void query(String filter, boolean isAttribute) {
        if (isAttribute)
            interactor.filterTrackEntities(filter);
        else
            interactor.addDateQuery(filter);
    }

    @Override
    public void setProgram(ProgramModel programSelected) {
        interactor.setProgram(programSelected);
    }

    @Override
    public void onBackClick() {
        view.back();
    }

    @Override
    public void onClearClick() {
        interactor.clear();
    }

    @Override
    public void requestCoordinates(LocationListener locationListener) {
        if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(view.getAbstracContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        } else {
            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null)
                locationListener.onLocationChanged(lastLocation);
            else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 0, locationListener);
            }
        }
    }

    @Override
    public void clearFilter(String uid) {
        interactor.clearFilter(uid);
    }

    @Override
    public void onEnrollClick(View view) {
        if (view.isEnabled())
            interactor.enroll();
        else
            this.view.displayMessage("Select a program to enable enrolling");
    }
}
