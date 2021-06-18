package it.polito.tdp.crimes.model;

import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	
	private SimpleWeightedGraph<String, DefaultWeightedEdge> grafo; //avendo una stringa come vertice non c'è bisogno della mappa
	private EventsDao dao; 
	private List<String> percorsoMigliore;
	
	public Model() {
		dao = new EventsDao();
	}
	
	public List<String> getCategorie(){
		return dao.getCategorie();
	}
	
	public void creaGrafo(String categoria, int mese) {
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		Graphs.addAllVertices(grafo, dao.getVertici(categoria, mese));
		
		for(Adiacenza a : dao.getAdiacenze(categoria, mese)) {
			if(this.grafo.getEdge(a.getV1(), a.getV2()) == null) {
				Graphs.addEdgeWithVertices(grafo, a.getV1(), a.getV2(), a.getPeso());
			}
		}
		System.out.println("v: " + this.grafo.vertexSet().size());
		System.out.println("a: " + this.grafo.edgeSet().size());
		
	}
	
	public List<Adiacenza> getArchi(){
		double pm = 0.0;
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			pm += this.grafo.getEdgeWeight(e);
		}
		
		pm = pm/this.grafo.edgeSet().size();
		
		List<Adiacenza> rs = new LinkedList<>();
		
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			if(this.grafo.getEdgeWeight(e)>pm)
				rs.add(new Adiacenza(this.grafo.getEdgeSource(e), this.grafo.getEdgeTarget(e), this.grafo.getEdgeWeight(e)));
		}
		
		return rs;
	}

	public List<String> trovaPercorso(String sorgente, String destinazione){
		this.percorsoMigliore = new LinkedList<>();
		List<String> parziale = new LinkedList<>();
		
		parziale.add(sorgente);
		cerca(destinazione, parziale);
		return this.percorsoMigliore;
		
	}
	
	private void cerca(String destinazione, List<String> parziale) {
		if(parziale.get(parziale.size()-1).equals(destinazione)) {
			if(parziale.size() > this.percorsoMigliore.size()) {
				this.percorsoMigliore = new LinkedList<> (parziale);
			}
			return;
		}
		for(String vicino : Graphs.neighborListOf(grafo, parziale.get(parziale.size()-1))) {
			if(!parziale.contains(vicino)) {
				parziale.add(vicino);
				cerca(destinazione, parziale);
				parziale.remove(parziale.size()-1);
			}
		}
	}
}
