package br.ufrj.caronae.frags;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.ufrj.caronae.R;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.adapters.CustomListAdapter;


public class CustomListFrag extends Fragment {

    CustomListAdapter adapter;
    static String[] list;
    RecyclerView rv;
    static String flag;

    public CustomListFrag() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CustomListFrag newInstance(String[] listReceived, String flagReceived) {
        CustomListFrag fragment = new CustomListFrag();

        list = listReceived;
        flag = flagReceived;

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_custom_list, container, false);

        rv = (RecyclerView) view.findViewById(R.id.rv);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        rv.setLayoutManager(manager);

        CustomListAdapter adapter = new CustomListAdapter(getActivity(), list, flag);

        rv.setAdapter(adapter);
        return view;
    }
}
