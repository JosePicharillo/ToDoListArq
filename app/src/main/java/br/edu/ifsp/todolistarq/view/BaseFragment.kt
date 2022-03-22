package br.edu.ifsp.todolistarq.view

import androidx.fragment.app.Fragment
import br.edu.ifsp.todolistarq.model.entity.Tarefa
import br.edu.ifsp.todolistarq.presenter.TarefaPresenter

abstract class BaseFragment: Fragment(), TarefaPresenter.TarefaView {
    // Constantes para serem usadas para comunicação entre os Fragments
    object Constantes {
        val TAREFA_REQUEST_KEY = "TAREFA_REQUEST_KEY"
        val TAREFA_EXTRA = "TAREFA_EXTRA"
        val ACAO_TAREFA_EXTRA = "ACAO_TAREFA_EXTRA"
        val CONSULTA = 1
    }

    override fun atualizarListaTarefas(listaTarefas: MutableList<Tarefa>) {    }
    override fun retornaTarefa(tarefa: Tarefa) {    }
}