package com.example.mosis_ispit.firstscreen.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mosis_ispit.addon.Validator;

public class LogInVM extends ViewModel {
//    private CompositeDisposable compositeDisposable = new CompositeDisposable();
//
//    private UserRepository userRepository;

    private MutableLiveData<String> checkFieldsSuccessful;
    private MutableLiveData<String> signInSuccessful;

//    public LogInVM(UserRepository userRepository) {
//        this.userRepository = userRepository;
//        checkFieldsSuccessful = new MutableLiveData<>();
//        signInSuccessful = new MutableLiveData<>();
//    }

    public LiveData<String> getSignInSuccessful() {
        return signInSuccessful;
    }

    public LiveData<String> getCheckFields() {
        return checkFieldsSuccessful;
    }

    public void checkFields(String username, String password) {
        try {
            Validator.isEmpty(username, "username");
            Validator.isEmpty(password, "password");


//            compositeDisposable.add(userRepository.signIn(username, password).subscribeOn(Schedulers.newThread())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribeWith(new DisposableObserver<AccessToken>() {
//                        @Override
//                        public void onNext(AccessToken accessToken) {
//                            signInSuccessful.setValue(accessToken.getAccessToken());
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//
//                            signInSuccessful.setValue("fail");
//                        }
//
//                        @Override
//                        public void onComplete() {
//
//                        }
//                    }));

        } catch (IllegalArgumentException e) {
            checkFieldsSuccessful.setValue(e.getMessage());
        }
    }


    @Override
    protected void onCleared() {
        super.onCleared();

//        compositeDisposable.clear();
    }
}
