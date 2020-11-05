package com.example.mosis_ispit.firstscreen.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mosis_ispit.addon.Validator;

public class RegisterVM extends ViewModel {
//    private CompositeDisposable compositeDisposable = new CompositeDisposable();
//
//    private UserRepository userRepository;

    private MutableLiveData<String> checkFieldsSuccessful;
    private MutableLiveData<String> signInSuccessful;
    private MutableLiveData<String> checkPassword;

//    public RegisterVM(UserRepository userRepository) {
//        this.userRepository = userRepository;
//        checkFieldsSuccessful = new MutableLiveData<>();
//        signInSuccessful = new MutableLiveData<>();
//        checkPassword = new MutableLiveData<>();
//    }

    public LiveData<String> getSignInSuccessful() {
        return signInSuccessful;
    }

    public LiveData<String> getCheckFields() {
        return checkFieldsSuccessful;
    }

    public LiveData<String> getCheckPassword() { return checkPassword; }

    public void checkFields(String firstName, String lastName, String username, String password, String confirmPassword) {
        try {
            Validator.isEmpty(firstName, "first name");
            Validator.isEmpty(lastName, "last name");
            Validator.isEmpty(username, "username");
            Validator.isEmpty(password, "password");
            Validator.isEmpty(confirmPassword, "confirm password");
            Validator.checkMatch(password, confirmPassword);

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
