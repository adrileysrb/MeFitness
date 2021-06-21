package com.example.mefitness.viewmodel;

import com.example.mefitness.model.Treino;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class FirestoreHelper {

    FirebaseFirestore db;
    DocumentReference documentReference;

    public FirestoreHelper(FirebaseFirestore instance) {
        db = instance;
    }

    public void updateExercicioEdit(String userID, String docID, int positionExercicio, String nome, String image, String observacoes){

        //Especificando qual campo a se efetuar a operação
        documentReference = db.collection(userID).document(docID);

        //Efetuando a operação
            documentReference.update("exercicioMap.exercicio-0"+positionExercicio+".nome", nome);
            documentReference.update("exercicioMap.exercicio-0"+positionExercicio+".image", image);
            documentReference.update("exercicioMap.exercicio-0"+positionExercicio+".observacoes", observacoes);
    }

    public void createData( String userID, Treino treino){

        //Especificando qual campo a se efetuar a operação
        documentReference = db.collection(userID).document();

        //Efetuando a operação
        documentReference.set(treino);
    }

    public void createDataExercicioAdd(FirebaseFirestore firebaseFirestore, int size, String userID, String docID, String nome, String image, String observacoes){

        DocumentReference doc = firebaseFirestore.collection(userID).document(docID);

        doc.update("exercicioMap.exercicio-0"+size+".nome", nome);
        doc.update("exercicioMap.exercicio-0"+size+".image", image);
        doc.update("exercicioMap.exercicio-0"+size+".observacoes", observacoes);
    }
}
