//package com.example.wuapp.ui.reportresult;
//
//import android.content.DialogInterface;
//import android.os.Bundle;
//import android.view.ContextThemeWrapper;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.fragment.app.Fragment;
//
//import com.airbnb.lottie.LottieAnimationView;
//import com.airbnb.lottie.LottieDrawable;
//import com.example.wuapp.R;
//import com.example.wuapp.ReportResultActivity;
//import com.example.wuapp.model.ReportFormState;
//import com.example.wuapp.model.WebLoader;
//
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Future;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.TimeoutException;
//
//public class SubmissionFragment extends Fragment {
//
//    private ReportFormState reportFormState;
//    private Future<String> OAuthToken;
//    private Map<String, String> cookies;
//
//    private List<Future<Boolean>> submissions = new ArrayList<>();
//    private List<LottieAnimationView> animations = new ArrayList<>();
//
//    private int finished = 0;
//    private String error_message = "";
//
//    public SubmissionFragment(){}
//
//    public SubmissionFragment(ReportFormState reportFormState, Future<String> OAuthToken, Map<String, String> cookies){
//        this.reportFormState = reportFormState;
//        this.OAuthToken = OAuthToken;
//        this.cookies = cookies;
//    }
//
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        View view =  inflater.inflate(R.layout.fragment_verify_submission, container, false);
//
//        //setup display
//        int formNumber = 0;
//        for(int i = 0; i < reportFormState.femaleMVPs.size(); i++){
//            submitMVP(reportFormState.getAllMvps().get(formNumber), formNumber);
//            buildReportingBox(view, "MVP Female #"+(i+1), formNumber);
//            formNumber++;
//        }
//        for(int i = 0; i < reportFormState.maleMVPs.size(); i++){
//            submitMVP(reportFormState.getAllMvps().get(formNumber), formNumber);
//            buildReportingBox(view, "MVP Male #"+(i+1), formNumber);
//
//            formNumber++;
//        }
//        submissions.add(WebLoader.submitFormState(cookies, reportFormState));
//        buildReportingBox(view, "Spirit Form", formNumber);
//
//        return view;
//    }
//
//    private void submitMVP(String name, int formNumber){
//        Document doc = reportFormState.doc;
//        List<String> oldMvps = WebLoader.parseReportFormPage(reportFormState.doc).getAllMvps(); //This is very fast
//        ReportResultActivity activity  = (ReportResultActivity) getActivity();
//
//        if(oldMvps.get(formNumber).equals(reportFormState.getAllMvps().get(formNumber))){ //mvp hasnt changed
//            submissions.add(formNumber, new Future<Boolean>() { //add dummy future that just returns true.
//                @Override
//                public boolean cancel(boolean b) {
//                    return false;
//                }
//
//                @Override
//                public boolean isCancelled() {
//                    return false;
//                }
//
//                @Override
//                public boolean isDone() {
//                    return true;
//                }
//
//                @Override
//                public Boolean get() throws ExecutionException, InterruptedException {
//                    return false;
//                }
//
//                @Override
//                public Boolean get(long l, TimeUnit timeUnit) throws ExecutionException, InterruptedException, TimeoutException {
//                    return true;
//                }
//            });
//            return;
//        }
//
//        Element mvpForm = doc.getElementsByClass("form-api live spacer-half keep-popup-open person-award-form").get(formNumber);
//        //get players ID
//        Element selectTag = mvpForm.getElementsByTag("select").first();
//        String playerID = getMVPId(selectTag, name);
//
//        //get other info needed for reporting mvp
//        Elements inputs = mvpForm.getElementsByTag("input");
//        String gameID = inputs.get(0).attr("value");
//        String teamID = inputs.get(1).attr("value");
//        String rank = inputs.get(2).attr("value");
//        String award = inputs.get(3).attr("value");
//
//        //build data map
//        Map<String, String> data = new HashMap<>();
//        data.put("person_id", playerID);
//        data.put("game_id", gameID);
//        data.put("team_id", teamID);
//        data.put("rank", rank);
//        data.put("award", award);
//
//        if (selectTag.child(0).attr("selected").equals("selected")) { //create award if value changed from blank to a name
//            submissions.add(formNumber, WebLoader.reportMVP(data, activity.getOAuthToken(), "https://wds.usetopscore.com/api/person-award/new"));
//
//        } else {
//            data.put("id", inputs.get(4).attr("value")); //set award id
//            if (name.equals("")) { //Delete award if value changed from a name to blank
//                data.remove("person_id"); //remove player id so award is given to no one
//                submissions.add(formNumber,WebLoader.reportMVP(data, activity.getOAuthToken(),"https://wds.usetopscore.com/api/person-award/delete"));
//            } else { //report new mvp
//                submissions.add(formNumber,WebLoader.reportMVP(data, activity.getOAuthToken(),"https://wds.usetopscore.com/api/person-award/edit"));
//            }
//        }
//    }
//
//    /**
//     * This function finds the id of a player from an html select tag where the players name is an option
//     * @param selectTag The encapsulating select tag
//     * @param MVPName The name of the player who id were are trying to get
//     * @return The id of the player or null if they weren't found
//     */
//    private String getMVPId(Element selectTag, String MVPName){
//        for(Element option : selectTag.children()){
//            if(option.text().contains(MVPName)){ //get mvp id from options
//                return option.attr("value");
//            }
//        }
//        return null;
//    }
//
//    private void buildReportingBox(View view, String text, int pos){
//        LottieAnimationView example_animation_view = view.findViewById(R.id.example_animation_view);
//        LinearLayout example_result_box = view.findViewById(R.id.example_result_box);
//        TextView example_display_text = view.findViewById(R.id.example_display_text);
//        LinearLayout result_display_box = view.findViewById(R.id.result_display_box);
//
//        LinearLayout result_box = new LinearLayout(getContext());
//        result_box.setOrientation(LinearLayout.HORIZONTAL);
//        result_box.setLayoutParams(example_result_box.getLayoutParams());
//
//        LottieAnimationView animationView = new LottieAnimationView(getContext());
//        animationView.setLayoutParams(example_animation_view.getLayoutParams());
//
//        animationView.setRepeatCount(LottieDrawable.INFINITE);
//        animationView.setAnimation(R.raw.loading_animation);
//        animationView.playAnimation();
//        animations.add(animationView);
//
//        animationView.addAnimatorUpdateListener((animation) -> {
//            if(submissions.get(pos) != null && submissions.get(pos).isDone()){ //submission is done
//                LottieAnimationView newAnimationView = new LottieAnimationView(getContext());
//                newAnimationView.setLayoutParams(animationView.getLayoutParams());
//                finished++;
//                try {
//                    Boolean result = submissions.get(pos).get();
//                    if(result){
//                        newAnimationView.setAnimation(R.raw.check_animation);
//                    } else {
//                        newAnimationView.setAnimation(R.raw.error_animation);
//                        error_message += text + " failed to submit\n";
//                    }
//                } catch (ExecutionException e) {
//                    newAnimationView.setAnimation(R.raw.error_animation);
//                    error_message += text + " failed to submit\n";
//                } catch (InterruptedException e) {
//                    newAnimationView.setAnimation(R.raw.error_animation);
//                    error_message += text + " failed to submit\n";
//                }
//            newAnimationView.setRepeatCount(0); //setup new animation
//            newAnimationView.setProgress(0f);
//            newAnimationView.playAnimation();
//                newAnimationView.addAnimatorUpdateListener((anim) -> {
//                    System.out.println(anim.getAnimatedValue());
//                    if((float)anim.getAnimatedValue() > 0.98f) {
//                        checkResults();
//                    }
//                });
//            animations.set(pos, newAnimationView);
//            result_box.removeView(animationView);
//            result_box.addView(newAnimationView, 0);
//            animationView.removeAllUpdateListeners();
//
//
//            }
//        });
//
//        TextView display_message = new TextView(getContext());
//        display_message.setText(text);
//        display_message.setTextColor(example_display_text.getCurrentTextColor());
//        display_message.setTextSize(18);
//        display_message.setLayoutParams(example_display_text.getLayoutParams());
//        result_box.setGravity(example_display_text.getGravity());
//
//        result_box.addView(animationView);
//        result_box.addView(display_message);
//        result_display_box.addView(result_box);
//    }
//
//    private void checkResults(){
//        if(finished == submissions.size()){
//            if(error_message.length() == 0){
//                getActivity().finish();
//            } else {
//                new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom))
//                        .setTitle("ERROR")
//                        .setMessage(error_message + "\n\nIf this error persists, please submit a bug report to " +
//                                "support.wuapp@gmail.com")
//                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                getActivity().finish();
//                            }
//                        })
//                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .show();
//            }
//
//        }
//    }
//
//
//
//
//}