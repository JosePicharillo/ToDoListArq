package br.edu.ifsp.todolistarq.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import br.edu.ifsp.todolistarq.R
import br.edu.ifsp.todolistarq.databinding.FragmentTarefaBinding
import br.edu.ifsp.todolistarq.model.database.ToDoListArqDatabase
import br.edu.ifsp.todolistarq.model.entity.Tarefa
import br.edu.ifsp.todolistarq.view.BaseFragment.Constantes.ACAO_TAREFA_EXTRA
import br.edu.ifsp.todolistarq.view.BaseFragment.Constantes.CONSULTA
import br.edu.ifsp.todolistarq.view.BaseFragment.Constantes.TAREFA_EXTRA
import br.edu.ifsp.todolistarq.view.BaseFragment.Constantes.TAREFA_REQUEST_KEY
import br.edu.ifsp.todolistarq.viewmodel.TarefaViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TarefaFragment : BaseFragment() {

    private lateinit var fragmentTarefaBinding: FragmentTarefaBinding
    private val ID_INEXISTENTE = -1L
    private var tarefaExtraId: Long = ID_INEXISTENTE
    private lateinit var database: ToDoListArqDatabase
    private lateinit var tarefaFragmentController: TarefaViewModel

    private var fab: FloatingActionButton? = null

    companion object {
        const val ACTION_UPDATE = "ACTION_UPDATE"
        const val ACTION_ADD = "ACTION_ADD"
        const val EXTRA_UPDATE = "UPDATE"
        const val EXTRA_ADD = "ADD"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //tarefaFragmentController = TarefaFragmentController(this)
        tarefaFragmentController =
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
                .create(TarefaViewModel::class.java)

        database = Room.databaseBuilder(
            requireContext(),
            ToDoListArqDatabase::class.java,
            ToDoListArqDatabase.Constantes.DB_NAME
        ).build()

        esconderFlagAdd()
    }

    private fun esconderFlagAdd() {
        fab = activity?.findViewById(R.id.novaTarefaFab)
        fab?.visibility = GONE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentTarefaBinding = FragmentTarefaBinding.inflate(inflater, container, false)

        fragmentTarefaBinding.salvarTarefaBt.setOnClickListener {
            if (tarefaExtraId != ID_INEXISTENTE) {
                // Atualiza Tarefa no BD
                tarefaFragmentController.atualizaTarefa(
                    Tarefa(
                        tarefaExtraId.toInt(),
                        fragmentTarefaBinding.nomeTarefaEt.text.toString(),
                        if (fragmentTarefaBinding.realizadaTarefaCb.isChecked) 1 else 0)
                )
            } else {
                // Insere Tarefa no BD
                tarefaFragmentController.insereTarefa(
                    Tarefa(
                        nome = fragmentTarefaBinding.nomeTarefaEt.text.toString(),
                        realizada = if (fragmentTarefaBinding.realizadaTarefaCb.isChecked) 1 else 0)
                )
            }
        }

        val tarefaExtra = arguments?.getParcelable<Tarefa>(TAREFA_EXTRA)
        if (tarefaExtra != null) {
            tarefaExtraId = tarefaExtra.id.toLong()
            with(fragmentTarefaBinding) {
                nomeTarefaEt.setText(tarefaExtra.nome)
                realizadaTarefaCb.isChecked =
                    tarefaExtra.realizada != 0 // False
            }
            val acaoTarefaExtra = arguments?.getInt(ACAO_TAREFA_EXTRA)
            if (acaoTarefaExtra == CONSULTA) {
                with(fragmentTarefaBinding) {
                    nomeTarefaEt.isEnabled = false
                    realizadaTarefaCb.isEnabled = false
                    salvarTarefaBt.visibility = GONE
                }
            }
        }

        return fragmentTarefaBinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        fab?.visibility = View.VISIBLE
    }

    override fun retornaTarefa(tarefa: Tarefa) {
        setFragmentResult(TAREFA_REQUEST_KEY, Bundle().also {
            it.putParcelable(TAREFA_EXTRA, tarefa)
        })
        activity?.supportFragmentManager?.popBackStack()
    }

    private val receiveAddTarefas: BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val bundle = intent?.extras
                val tarefa = bundle?.getParcelable<Tarefa>(EXTRA_ADD)
                retornaTarefa(tarefa!!)
            }
        }
    }

    private val receiveUpdateTarefas: BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val bundle = intent?.extras
                val tarefa = bundle?.getParcelable<Tarefa>(EXTRA_UPDATE)
                retornaTarefa(tarefa!!)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        requireActivity().registerReceiver(receiveAddTarefas,
            IntentFilter(ACTION_ADD))

        requireActivity().registerReceiver(receiveUpdateTarefas,
            IntentFilter(ACTION_UPDATE))
    }
}