package nasaaty.com.hm.utils.repos;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.model.Review;
import nasaaty.com.hm.model.User;

public class TransactionRepository {

	private Context context;
	private FirebaseFirestore firestore;
	private CollectionReference statsRef;
	private CollectionReference likesRef;
	private FirebaseAuth auth;
	private Boolean processViews;
	numberOfViews numberOfViews;
	onViewNodeCreated onViewNodeCreated;
	onViewAdded onViewAdded;
	onViewRemoved onViewRemoved;
	private Boolean mProcessLike = true;

	public TransactionRepository(Context context) {
		this.context = context;
		this.firestore = FirebaseFirestore.getInstance();
		this.statsRef = firestore.collection("stats");
		this.likesRef = firestore.collection("likes");
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
						if (queryDocumentSnapshots.isEmpty()) {
							Toast.makeText(context, "empty", Toast.LENGTH_SHORT).show();
							addView(pid);
						} else {
							Toast.makeText(context, "kkd", Toast.LENGTH_SHORT).show();
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

	public void getViewCount(String pid, final numberOfViews number) {
		statsRef.document("views").collection(pid)
				.addSnapshotListener(new EventListener<QuerySnapshot>() {
					@Override
					public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
						if (e != null) return;

						int count = 0;
						for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
							count++;
						}
						number.count(count);
						Toast.makeText(context, String.valueOf(count), Toast.LENGTH_SHORT).show();
					}
				});
	}

	//---LIKES---
	public void allowUserLikes(final String pid) {
		final String uid = auth.getCurrentUser().getUid();
		final Map<String, Boolean> user = new HashMap<>();
		user.put(uid, true);

		likesRef.document(pid)
				.addSnapshotListener(new EventListener<DocumentSnapshot>() {
					@Override
					public void onEvent(@Nullable final DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
						if (e != null) return;
						final CollectionReference uRef = documentSnapshot.getReference().collection(uid);
						if (documentSnapshot.exists()){

							uRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
								@Override
								public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
									if (e != null) return;

									mProcessLike = true;
									for (DocumentSnapshot snapshot : queryDocumentSnapshots){
										if (snapshot.get(uid) != null){
											uRef.document(snapshot.getId()).delete();
											mProcessLike = true;
											break;
										}else {
											if (mProcessLike = false){
												likesRef.document(pid).collection(uid).add(user);
												mProcessLike = false;
											}
//										mProcessLike = false;
											Toast.makeText(context, "else", Toast.LENGTH_SHORT).show();
										}
									}
								}
							});
						}else {
							if (mProcessLike){
								likesRef.document(pid).collection(uid).add(user);
								mProcessLike = false;
							}
						}
					}
				});

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

	//Reviews
	public Task<Void> addRating(String pid, final Review review, final Float rate){
		final DocumentReference proRef = firestore.collection("products").document(pid);

		final DocumentReference ratingRef = proRef.collection("ratings").document(review.getUid());
		final DocumentReference reviewRef = proRef.collection("reviews").document(review.getUid());

		return firestore.runTransaction(new Transaction.Function<Void>() {
			@android.support.annotation.Nullable
			@Override
			public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

				final Product product = transaction.get(proRef).toObject(Product.class);

				//Compute new number of ratings
				final int newNumRating = product.numRatings + 1;

				//Compute new average rating
				double oldRatingTotal = product.avgRatings * product.numRatings;
				double newAvgRating = (oldRatingTotal + rate) / newNumRating;

				//set new restaurant info
				ratingRef.get()
						.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
							@Override
							public void onSuccess(DocumentSnapshot documentSnapshot) {
								if (documentSnapshot.get(review.getUid()) != null){
									Toast.makeText(context, "can't update nums", Toast.LENGTH_SHORT).show();
								}else {
									product.numRatings = newNumRating;
								}
							}
						});
				product.avgRatings = newAvgRating;

				//Update restaurant
				transaction.set(proRef, product);

				//update rating
				Map<String, Object> data = new HashMap<>();
				data.put("rating", rate);
				transaction.set(ratingRef, data, SetOptions.merge());

				//update review
				Map<String, String> rev = new HashMap<>();
				rev.put(review.getUid(), review.getReview());
				transaction.set(reviewRef, review, SetOptions.merge());

			    return null;
			}
		});
	}

	public void checkUserReview(String uid, final String pid, final isUserReviewFound isUserReviewFound){
		final DocumentReference proRef = firestore.collection("products").document(pid);
		final DocumentReference ratingRef = proRef.collection("ratings").document(uid);
		final DocumentReference reviewRef = proRef.collection("reviews").document(uid);

		ratingRef.get()
				.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
					@Override
					public void onSuccess(DocumentSnapshot documentSnapshot) {
						if (documentSnapshot.exists()){

							reviewRef.get()
									.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
										@Override
										public void onSuccess(DocumentSnapshot documentSnapshot) {
											if (documentSnapshot.exists()){
												final Review review = documentSnapshot.toObject(Review.class);
												proRef.get()
														.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
															@Override
															public void onSuccess(DocumentSnapshot documentSnapshot) {
																if (documentSnapshot.exists()){
																	final Product product = documentSnapshot.toObject(Product.class);

																	if (product.getPid().equals(pid)){
																		Query query = firestore.collection("users").whereEqualTo("uid", product.getOwner());
																		query.addSnapshotListener(new EventListener<QuerySnapshot>() {
																			@Override
																			public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
																				if (e != null) return;

																				for (DocumentSnapshot snapshot : queryDocumentSnapshots){
																					if (snapshot.exists()){
																						User user = snapshot.toObject(User.class);
																						isUserReviewFound.isFound(true, review, product, user);
																					}
																				}
																			}
																		});
																	}


																}
															}
														});

											}
										}
									});

						}else {
							isUserReviewFound.isFound(false, null, null, null);
						}
					}
				});

	}

	public interface isUserReviewFound{
		void isFound(Boolean found, Review review, Product product, User user);
	}


}
