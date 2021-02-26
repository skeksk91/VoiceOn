package com.example.voiceon

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room.databaseBuilder as DatabaseBuilder
import com.example.voiceon.Room


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var deleteSentVoice: ImageView
    private lateinit var hearingView: ImageView
    private lateinit var deleteAll: ImageView
    private var rooms = ArrayList<Room>()

    private var isLongClicked: Boolean = false
    private var isHearing: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = context?.applicationContext?.let {
            DatabaseBuilder(
                it,
                AppDatabase::class.java, "room"
            ).build()
        }

        // TODO: Only once!
        var ret : List<Room>? = null
        val t = Runnable {
            //db?.roomDao()?.insertRooms(Room("1", "익명의 갈매기", false))
            //db?.roomDao()?.insertRooms(Room("2", "익명의 나나", false))
            //db?.roomDao()?.insertRooms(Room("3", "익명의 삼엽충", false))
            ret = db?.roomDao()?.getAll()
            if (ret != null) {
                for(room in ret!!) {
                    rooms.add(Room(room.uid, room.uName, room.isLike))
                }
                Log.e("jake", "room Added in db thread")
            }
        }
        var thread = Thread(t)
        thread.start()
        thread.join()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rooms = ArrayList(rooms.sortedWith(compareBy { !it.isLike!! }))

        recyclerView = view.findViewById(R.id.room_list_view)

        recyclerView.apply {
            recyclerView.layoutManager = LinearLayoutManager(context)

            Log.e("jake", "onViewCreated = " + rooms.size)
            recyclerView.adapter = RoomListAdapter(rooms,
                clickListener = {
                    var bundle: Bundle = Bundle()
                    bundle.putSerializable("room", it)
                    findNavController().navigate(R.id.action_listFragment_to_RoomFragment, bundle)
                }
            ) {
                Toast.makeText(context, "LongClicked", Toast.LENGTH_SHORT).show()
                // LongClickListener
                adapter?.notifyDataSetChanged()
            }
        }

        recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        deleteSentVoice = view.findViewById(R.id.delete_sent_voice)
        deleteSentVoice.setOnClickListener {
            val popup = AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
                .setTitle(getString(R.string.delete_sent_message))
                .setPositiveButton(getString(R.string.delete)) { dialog, which ->
                    //Toast.makeText(context, "메아리가 삭제됐습니다.", Toast.LENGTH_LONG).show()
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .create()
            popup.show()
        }

        hearingView = view.findViewById(R.id.hearing)
        hearingView.setOnClickListener {
            if (isHearing) {
                Toast.makeText(context, getString(R.string.not_hearing), Toast.LENGTH_LONG).show()
                hearingView.setImageResource(R.drawable.ic_hearing_disabled_24px)
            } else {
                Toast.makeText(context, getString(R.string.not_hearing_cancel), Toast.LENGTH_LONG).show()
                hearingView.setImageResource(R.drawable.ic_hearing_24px_light)
            }
            isHearing = !isHearing
        }

        // TODO: deleteAll in list adapter.
        deleteAll = view.findViewById(R.id.delete_all)
        deleteAll.setOnClickListener {
            val popup = AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
                .setTitle(getString(R.string.delete_all))
                .setPositiveButton(getString(R.string.delete)) { dialog, which ->
                    val iterator = rooms.iterator()
                     while (iterator.hasNext()) {
                         val item = iterator.next()
                         if (!item.isLike!!) {
                             val pos = rooms.indexOf(item)
                             recyclerView.adapter?.notifyItemRemoved(pos)
                             recyclerView.adapter?.notifyItemRangeChanged(pos, rooms.size)
                         }
                    }
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        rooms.removeIf { !it.isLike!! }
                    }
                    Toast.makeText(context, "좋아요하지 않은 방들을 삭제했습니다.", Toast.LENGTH_LONG).show()
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .create()
            popup.show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}