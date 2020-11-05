package com.example.mosis_ispit.addon;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TokenData {
    public Location currentLocation = null;
    ArrayList<Token> myTokens;
    private DatabaseReference database;
    private static final String FIREBASE_CHILD="tokens";
    public TokenData(){
        this.myTokens = new ArrayList<Token>();
        //myPlacesKeyIndexMapping= new HashMap<String,Integer>();
        database= FirebaseDatabase.getInstance().getReference();
        resetListeners();
    }

    public void resetListeners(){
        database.child(FIREBASE_CHILD).addChildEventListener(childEventListener);
        database.child(FIREBASE_CHILD).addListenerForSingleValueEvent(parentEventListener);
    }

    ValueEventListener parentEventListener=new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
           /* if(updateListener!=null)
                updateListener.onListUpdated();*/
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    ChildEventListener childEventListener=new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            String myTokenKey=dataSnapshot.getKey();
            Token myToken=dataSnapshot.getValue(Token.class);
            myToken.key=myTokenKey;
            myTokens.add(myToken);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            String myTokenkey=dataSnapshot.getKey();
            Token deletedToken=dataSnapshot.getValue(Token.class);
            deletedToken.key = myTokenkey;
            for (int i=0;i<myTokens.size();i++)
            {
                if(deletedToken.key.equals(myTokens.get(i).key)){
                    myTokens.remove(i);
                    i--;
                }
            }
            //myTokens.remove(deletedToken);
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    private  static class SingletonHolder{
        private static final TokenData instance = new TokenData();
    }
    public static TokenData getInstance(){
        return  TokenData.SingletonHolder.instance;
    }

    public ArrayList<Token> getMyTokens(){
        return  myTokens;
    }
    public Token getToken(int index){
        return this.myTokens.get(index);
    }

    public void init(){

    }
}
