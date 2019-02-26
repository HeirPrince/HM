package nasaaty.com.hm.utils.repos;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class TransactionRepository {

	private Context context;
	private FirebaseFirestore firestore;
	private CollectionReference statsRef;
	private FirebaseAuth auth;
	private Boolean processViews;
	numberOfViews numberOfViews;
	onViewNodeCreated onViewNodeCreated;
	onViewAdded onViewAdded;
	onViewRemoved onViewRemoved;

	public TransactionRepository(Context context) {
		this.context = context;
		this.firestore = FirebaseFirestore.getInstance();
		this.statsRef = firestore.collection("stats");
		this.processViews = false;
		this.auth = FirebaseAuth.getInstance();
	}


	public void trans(final String pid) {
		final CollectionReference vPRef = statsRef.document("views").collection(pid);

		final Map<String, Double> view = new HashMap<>();
		view.put("nmViews", 0.0);

		vPRef
				.addSnapshotListener(new EventListener<QuerySnapshot>() {
					@Override
					public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
						if (e != null)
							return;
						if (queryDocumentSnapshots.isEmpty()){
							Toast.makeText(context, "empty", Toast.LENGTH_SHORT).show();
							addView(pid);
						}else {
							Toast.makeText(context, "kkd", Toast.LENGTH_SHORT).show();
							return;
						}
					}
				});

	}

	private void addView(final String pid) {

		final Map<String, Boolean> viewer = new HashMap<>();
		viewer.put(auth.getCurrentUser().getUid(), true);

		statsRef.document("views").collection(pid)
				.add(viewer)
				.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
					@Override
					public void onSuccess(DocumentReference documentReference) {
						Toast.makeText(context, "view added", Toast.LENGTH_SHORT).show();
					}
				});
	}

	public void getViewCount(String pid, final numberOfViews number){
		statsRef.document("views").collection(pid)
				.addSnapshotListener(new EventListener<QuerySnapshot>() {
					@Override
					public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
						if (e != null)return;

						int count = 0;
						for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){
							count++;
						}
						number.count(count);
						Toast.makeText(context, String.valueOf(count), Toast.LENGTH_SHORT).show();
					}
				});
	}

	//---LIKES---
	public void allowUserLikes(final String pid){
		final String uid = auth.getCurrentUser().getUid();
		final Map<String, Boolean> user = new HashMap<>();
		user.put(uid, true);
		Boolean isLiked = false;

		firestore.collection("likes").document(pid).collection("liked")
				.addSnapshotListener(new EventListener<QuerySnapshot>() {
					@Override
					public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
						if (e != null) return;

						if (queryDocumentSnapshots.isEmpty()){
							setUser(pid, user);
						}else {
							for (DocumentSnapshot snapshot : queryDocumentSnapshots){
								if (snapshot.exists()){
									if (snapshot.get(uid) == null){
										//
										return;
									}else {
										Toast.makeText(context, "isLiked", Toast.LENGTH_SHORT).show();
									}
								}else {
//									Toast.makeText(context, "new like", Toast.LENGTH_SHORT).show();
									setUser(pid, user);
								}
							}
						}
					}
				});
	}

	private void setUser(final String pid, Map<String, Boolean> user) {
		firestore.collection("likes").document(pid).collection("liked")
				.add(user).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
			@Override
			public void onComplete(@NonNull Task<DocumentReference> task) {
				doLikeTransaction(pid);
			}
		});
	}

	private void doLikeTransaction(final String pid) {
		final Map<String, Double> like = new HashMap<>();
		like.put("likes", 0.0);

		final DocumentReference likeRef = firestore.collection("likes").document(pid);

		likeRef
				.get()
				.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
					@Override
					public void onSuccess(DocumentSnapshot documentSnapshot) {
						if (documentSnapshot.exists()){
							firestore.collection("likes").document(pid)
									.set(like);
							firestore.runTransaction(new Transaction.Function<Void>() {
								@android.support.annotation.Nullable
								@Override
								public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
									DocumentSnapshot snapshot = transaction.get(likeRef);
									Double newLike = snapshot.getDouble("likes") + 1;
									transaction.update(likeRef, "likes", newLike);

									return null;
								}
							});
						}
						else {
							firestore.collection("likes").document(pid)
									.set(like);
						}
					}
				});


	}

	public void countDocs(){
		
	}

	public interface numberOfViews {
		void count(int n);
	}

	public interface onViewNodeCreated {
		void onComplete(Boolean complete);
	}

	public interface onViewAdded {
		void onAdded(Boolean complete);
	}

	public interface onViewRemoved {
		void onRemoved(Boolean complete);
	}


}
