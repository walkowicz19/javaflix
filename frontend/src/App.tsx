import React, { useEffect, useState } from "react";
import { Navbar } from "./components/Navbar";
import { Hero } from "./components/Hero";
import { Row } from "./components/Row";
import { getCatalogo } from "./services/api";
import type { Conteudo, Filme, Serie } from "./types";

function App() {
  const [catalogo, setCatalogo] = useState<Conteudo[]>([]);
  const [loading, setLoading] = useState(true);
  const [modal, setModal] = useState<null | { type: string; data?: any }>(null);
  const [selected, setSelected] = useState<Conteudo | null>(null);

  useEffect(() => {
    const fetchCatalogo = async () => {
      const data = await getCatalogo();
      setCatalogo(data);
      setLoading(false);
    };
    fetchCatalogo();
  }, []);

  if (loading) {
    return (
      <div className="min-h-screen bg-netflix-black flex items-center justify-center">
        <div className="text-netflix-primary text-2xl font-bold">
          Carregando...
        </div>
      </div>
    );
  }

  // Filter content for rows
  const filmes = catalogo.filter((c) => c.tipo === "Filme");
  const series = catalogo.filter((c) => c.tipo === "Serie");
  const dramas = catalogo.filter((c) =>
    c.genero.toLowerCase().includes("drama")
  );
  const ficcao = catalogo.filter(
    (c) =>
      c.genero.toLowerCase().includes("ficção") ||
      c.genero.toLowerCase().includes("ficcao")
  );

  // Modal content
  const renderModal = () => {
    if (!modal) return null;
    if (modal.type === "search") {
      return (
        <div className="fixed inset-0 bg-black/70 flex items-center justify-center z-[999]">
          <div className="bg-zinc-900 p-8 rounded-lg w-full max-w-md">
            <h2 className="text-xl font-bold mb-4 text-white">Buscar</h2>
            <input
              className="w-full p-2 rounded bg-zinc-800 text-white mb-4"
              placeholder="Digite o nome do filme ou série..."
              autoFocus
            />
            <button
              className="mt-2 px-4 py-2 bg-netflix-primary rounded text-black font-bold"
              onClick={() => setModal(null)}
            >
              Fechar
            </button>
          </div>
        </div>
      );
    }
    if (modal.type === "notifications") {
      return (
        <div className="fixed inset-0 bg-black/70 flex items-center justify-center z-[999]">
          <div className="bg-zinc-900 p-8 rounded-lg w-full max-w-md">
            <h2 className="text-xl font-bold mb-4 text-white">Notificações</h2>
            <p className="text-gray-300">Nenhuma notificação no momento.</p>
            <button
              className="mt-4 px-4 py-2 bg-netflix-primary rounded text-black font-bold"
              onClick={() => setModal(null)}
            >
              Fechar
            </button>
          </div>
        </div>
      );
    }
    if (modal.type === "user") {
      return (
        <div className="fixed inset-0 bg-black/70 flex items-center justify-center z-[999]">
          <div className="bg-zinc-900 p-8 rounded-lg w-full max-w-md">
            <h2 className="text-xl font-bold mb-4 text-white">
              Preferências do Usuário
            </h2>
            <p className="text-gray-300">
              Configurações e preferências do usuário aqui.
            </p>
            <button
              className="mt-4 px-4 py-2 bg-netflix-primary rounded text-black font-bold"
              onClick={() => setModal(null)}
            >
              Fechar
            </button>
          </div>
        </div>
      );
    }
    if (modal.type === "details" && selected) {
      const isFilme = selected.tipo === "Filme";
      const isSerie = selected.tipo === "Serie";
      return (
        <div className="fixed inset-0 bg-black/80 flex items-center justify-center z-[999]">
          <div className="bg-zinc-900 p-6 rounded-lg w-full max-w-lg relative">
            <button
              className="absolute top-2 right-2 text-white text-2xl"
              onClick={() => setModal(null)}
            >
              &times;
            </button>
            <h2 className="text-2xl font-bold text-white mb-2">
              {selected.titulo}
            </h2>
            <p className="text-gray-300 mb-2">{selected.genero}</p>
            <p className="text-gray-400 mb-4">
              {isFilme &&
                `Duração: ${(selected as Filme).duracaoMinutos || "-"} min`}
              {isSerie &&
                `Temporadas: ${
                  (selected as Serie).temporadas || "-"
                }, Episódios/Temp: ${
                  (selected as Serie).episodiosPorTemporada || "-"
                }`}
            </p>
            <p className="text-gray-200 mb-4">
              Classificação:{" "}
              {selected.classificacao === 0
                ? "Livre"
                : `+${selected.classificacao}`}
            </p>
            <button className="px-6 py-3 bg-netflix-primary text-black font-bold rounded hover:bg-yellow-400 transition transform hover:scale-105">
              Assistir
            </button>
          </div>
        </div>
      );
    }
    return null;
  };

  // Card click handler
  const handleCardClick = (item: Conteudo) => {
    setSelected(item);
    setModal({ type: "details" });
  };

  return (
    <div className="bg-netflix-black min-h-screen pb-20 overflow-x-hidden">
      <Navbar
        onSearch={() => setModal({ type: "search" })}
        onNotifications={() => setModal({ type: "notifications" })}
        onUser={() => setModal({ type: "user" })}
      />
      <div id="inicio">
        <Hero />
      </div>
      <div className="-mt-32 relative z-10">
        <div id="minha-lista">
          {catalogo.length > 0 && (
            <Row
              title="Minha Lista"
              items={catalogo}
              onCardClick={handleCardClick}
            />
          )}
        </div>
        <div id="filmes">
          {filmes.length > 0 && (
            <Row
              title="Filmes em Alta"
              items={filmes}
              onCardClick={handleCardClick}
            />
          )}
        </div>
        <div id="series">
          {series.length > 0 && (
            <Row
              title="Séries Maratonáveis"
              items={series}
              onCardClick={handleCardClick}
            />
          )}
        </div>
        <div id="bombando">
          {dramas.length > 0 && (
            <Row
              title="Dramas Aclamados"
              items={dramas}
              onCardClick={handleCardClick}
            />
          )}
        </div>
        <div id="ficcao">
          {ficcao.length > 0 && (
            <Row
              title="Ficção Científica e Fantasia"
              items={ficcao}
              onCardClick={handleCardClick}
            />
          )}
        </div>
      </div>
      {renderModal()}
    </div>
  );
}

export default App;
