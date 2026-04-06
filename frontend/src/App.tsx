import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Play, Info, Search, Bell, X } from 'lucide-react';
import { Navbar } from "./components/Navbar";
import { Hero } from "./components/Hero";
import { Row } from "./components/Row";
import { getCatalogo } from "./services/api";
import type { Conteudo, Filme, Serie } from "./types";

function App() {
  const navigate = useNavigate();
  const [catalogo, setCatalogo] = useState<Conteudo[]>([]);
  const [loading, setLoading] = useState(true);
  const [modal, setModal] = useState<null | { type: string; data?: any }>(null);
  const [selected, setSelected] = useState<Conteudo | null>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState<Conteudo[]>([]);
  const [userTab, setUserTab] = useState<'preferences' | 'plan'>('preferences');

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

  // Handle search
  const handleSearch = (query: string) => {
    setSearchQuery(query);
    if (query.trim()) {
      const results = catalogo.filter(item =>
        item.titulo.toLowerCase().includes(query.toLowerCase()) ||
        item.genero.toLowerCase().includes(query.toLowerCase())
      );
      setSearchResults(results);
    } else {
      setSearchResults([]);
    }
  };

  // Modal content
  const renderModal = () => {
    if (!modal) return null;
    
    if (modal.type === "search") {
      return (
        <div className="fixed inset-0 bg-black/90 flex items-start justify-center z-[999] pt-20 px-4" onClick={() => setModal(null)}>
          <div className="bg-zinc-900 rounded-lg w-full max-w-2xl max-h-[80vh] overflow-hidden" onClick={(e) => e.stopPropagation()}>
            <div className="p-6 border-b border-zinc-800">
              <div className="flex items-center gap-3">
                <Search className="w-5 h-5 text-gray-400" />
                <input
                  className="flex-1 bg-transparent text-white text-lg outline-none"
                  placeholder="Buscar filmes, séries, gêneros..."
                  value={searchQuery}
                  onChange={(e) => handleSearch(e.target.value)}
                  autoFocus
                />
                <button onClick={() => setModal(null)} className="text-gray-400 hover:text-white">
                  <X className="w-6 h-6" />
                </button>
              </div>
            </div>
            <div className="p-6 overflow-y-auto max-h-[calc(80vh-100px)]">
              {searchResults.length > 0 ? (
                <div className="space-y-3">
                  {searchResults.map((item, index) => (
                    <div
                      key={index}
                      className="flex gap-4 p-3 bg-zinc-800 rounded-lg hover:bg-zinc-700 cursor-pointer transition"
                      onClick={() => {
                        setSelected(item);
                        setModal({ type: "details" });
                      }}
                    >
                      <div className="w-20 h-28 bg-zinc-700 rounded flex-shrink-0"></div>
                      <div className="flex-1">
                        <h3 className="text-white font-bold mb-1">{item.titulo}</h3>
                        <p className="text-sm text-gray-400 mb-2">{item.genero}</p>
                        <div className="flex items-center gap-2">
                          <span className="text-xs px-2 py-1 bg-green-600 text-white rounded">
                            {item.classificacao === 0 ? 'Livre' : `+${item.classificacao}`}
                          </span>
                          <span className="text-xs text-gray-400">{item.tipo}</span>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              ) : searchQuery ? (
                <p className="text-gray-400 text-center py-8">Nenhum resultado encontrado para "{searchQuery}"</p>
              ) : (
                <p className="text-gray-400 text-center py-8">Digite para buscar conteúdos</p>
              )}
            </div>
          </div>
        </div>
      );
    }
    
    if (modal.type === "notifications") {
      const notifications = [
        { id: 1, title: "Novo episódio disponível", content: "Stranger Things - Temporada 4, Episódio 9", time: "Há 2 horas", new: true },
        { id: 2, title: "Adicionado à sua lista", content: "Breaking Bad foi adicionado com sucesso", time: "Há 5 horas", new: true },
        { id: 3, title: "Recomendação para você", content: "Baseado no seu histórico, você pode gostar de Dark", time: "Ontem", new: false },
        { id: 4, title: "Lançamento em breve", content: "Matrix 5 estreia em 15 dias", time: "Há 2 dias", new: false },
      ];
      
      return (
        <div className="fixed inset-0 bg-black/70 flex items-center justify-center z-[999] px-4" onClick={() => setModal(null)}>
          <div className="bg-zinc-900 rounded-lg w-full max-w-md max-h-[80vh] overflow-hidden" onClick={(e) => e.stopPropagation()}>
            <div className="p-6 border-b border-zinc-800 flex items-center justify-between">
              <h2 className="text-xl font-bold text-white flex items-center gap-2">
                <Bell className="w-5 h-5" />
                Notificações
              </h2>
              <button onClick={() => setModal(null)} className="text-gray-400 hover:text-white">
                <X className="w-6 h-6" />
              </button>
            </div>
            <div className="overflow-y-auto max-h-[calc(80vh-100px)]">
              {notifications.map((notif) => (
                <div key={notif.id} className={`p-4 border-b border-zinc-800 hover:bg-zinc-800 cursor-pointer transition ${notif.new ? 'bg-zinc-800/50' : ''}`}>
                  <div className="flex items-start gap-3">
                    {notif.new && <div className="w-2 h-2 bg-netflix-primary rounded-full mt-2 flex-shrink-0"></div>}
                    <div className="flex-1">
                      <h3 className="text-white font-semibold mb-1">{notif.title}</h3>
                      <p className="text-sm text-gray-400 mb-2">{notif.content}</p>
                      <span className="text-xs text-gray-500">{notif.time}</span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      );
    }
    
    if (modal.type === "user") {
      const currentProfile = JSON.parse(localStorage.getItem('selectedProfile') || '{"name":"Usuário"}');
      
      return (
        <div className="fixed inset-0 bg-black/70 flex items-center justify-center z-[999] px-4" onClick={() => setModal(null)}>
          <div className="bg-zinc-900 rounded-lg w-full max-w-2xl max-h-[90vh] overflow-hidden" onClick={(e) => e.stopPropagation()}>
            <div className="p-6 border-b border-zinc-800">
              <div className="flex items-center justify-between mb-4">
                <h2 className="text-xl font-bold text-white">Conta</h2>
                <button onClick={() => setModal(null)} className="text-gray-400 hover:text-white">
                  <X className="w-6 h-6" />
                </button>
              </div>
              {/* Current Profile Info */}
              <div className="flex items-center gap-3 p-3 bg-zinc-800 rounded-lg">
                <div className="w-12 h-12 rounded-full overflow-hidden bg-zinc-700">
                  {currentProfile.avatar && (
                    <img src={currentProfile.avatar} alt={currentProfile.name} className="w-full h-full object-cover" />
                  )}
                </div>
                <div className="flex-1">
                  <p className="text-white font-semibold">{currentProfile.name}</p>
                  <p className="text-sm text-gray-400">Perfil ativo</p>
                </div>
                <button
                  onClick={() => navigate('/')}
                  className="px-4 py-2 bg-zinc-700 text-white text-sm font-semibold rounded hover:bg-zinc-600 transition"
                >
                  Trocar Perfil
                </button>
              </div>
            </div>
            
            {/* Tabs */}
            <div className="flex border-b border-zinc-800">
              <button
                className={`flex-1 px-6 py-3 font-semibold transition ${
                  userTab === 'preferences'
                    ? 'text-white border-b-2 border-netflix-primary'
                    : 'text-gray-400 hover:text-white'
                }`}
                onClick={() => setUserTab('preferences')}
              >
                Preferências
              </button>
              <button
                className={`flex-1 px-6 py-3 font-semibold transition ${
                  userTab === 'plan'
                    ? 'text-white border-b-2 border-netflix-primary'
                    : 'text-gray-400 hover:text-white'
                }`}
                onClick={() => setUserTab('plan')}
              >
                Plano e Pagamento
              </button>
            </div>

            <div className="p-6 overflow-y-auto max-h-[calc(90vh-180px)]">
              {userTab === 'preferences' ? (
                <div className="space-y-4">
                  <div>
                    <label className="block text-sm font-semibold text-white mb-2">Idioma</label>
                    <select className="w-full p-2 bg-zinc-800 text-white rounded border border-zinc-700 focus:border-netflix-primary outline-none">
                      <option>Português (Brasil)</option>
                      <option>English</option>
                      <option>Español</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-white mb-2">Qualidade de Vídeo</label>
                    <select className="w-full p-2 bg-zinc-800 text-white rounded border border-zinc-700 focus:border-netflix-primary outline-none">
                      <option>Auto</option>
                      <option>Alta (1080p)</option>
                      <option>Média (720p)</option>
                      <option>Baixa (480p)</option>
                    </select>
                  </div>
                  <div className="flex items-center justify-between py-2">
                    <span className="text-white font-semibold">Reprodução Automática</span>
                    <label className="relative inline-block w-12 h-6">
                      <input type="checkbox" className="sr-only peer" defaultChecked />
                      <div className="w-full h-full bg-zinc-700 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-netflix-primary"></div>
                    </label>
                  </div>
                  <div className="flex items-center justify-between py-2">
                    <span className="text-white font-semibold">Notificações</span>
                    <label className="relative inline-block w-12 h-6">
                      <input type="checkbox" className="sr-only peer" defaultChecked />
                      <div className="w-full h-full bg-zinc-700 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-netflix-primary"></div>
                    </label>
                  </div>
                  <button className="w-full mt-4 px-4 py-3 bg-netflix-primary text-black font-bold rounded hover:bg-yellow-400 transition">
                    Salvar Preferências
                  </button>
                </div>
              ) : (
                <div className="space-y-6">
                  {/* Current Plan */}
                  <div className="bg-zinc-800 p-5 rounded-lg border-2 border-netflix-primary">
                    <div className="flex items-center justify-between mb-3">
                      <h3 className="text-lg font-bold text-white">Plano Atual</h3>
                      <span className="px-3 py-1 bg-netflix-primary text-black text-xs font-bold rounded-full">ATIVO</span>
                    </div>
                    <div className="space-y-2">
                      <div className="flex justify-between">
                        <span className="text-gray-400">Plano:</span>
                        <span className="text-white font-semibold">Premium</span>
                      </div>
                      <div className="flex justify-between">
                        <span className="text-gray-400">Valor:</span>
                        <span className="text-white font-semibold">R$ 55,90/mês</span>
                      </div>
                      <div className="flex justify-between">
                        <span className="text-gray-400">Próxima cobrança:</span>
                        <span className="text-white font-semibold">15/05/2026</span>
                      </div>
                      <div className="flex justify-between">
                        <span className="text-gray-400">Qualidade:</span>
                        <span className="text-white font-semibold">4K + HDR</span>
                      </div>
                      <div className="flex justify-between">
                        <span className="text-gray-400">Telas simultâneas:</span>
                        <span className="text-white font-semibold">4 dispositivos</span>
                      </div>
                    </div>
                  </div>

                  {/* Available Plans */}
                  <div>
                    <h3 className="text-lg font-bold text-white mb-4">Outros Planos Disponíveis</h3>
                    <div className="space-y-3">
                      {/* Basic Plan */}
                      <div className="bg-zinc-800 p-4 rounded-lg hover:bg-zinc-700 transition cursor-pointer">
                        <div className="flex items-start justify-between mb-2">
                          <div>
                            <h4 className="text-white font-bold">Básico</h4>
                            <p className="text-2xl font-bold text-white mt-1">R$ 25,90<span className="text-sm text-gray-400">/mês</span></p>
                          </div>
                        </div>
                        <ul className="space-y-1 text-sm text-gray-300 mt-3">
                          <li>✓ Qualidade HD (720p)</li>
                          <li>✓ 1 tela simultânea</li>
                          <li>✓ Catálogo completo</li>
                          <li>✓ Download em 1 dispositivo</li>
                        </ul>
                      </div>

                      {/* Standard Plan */}
                      <div className="bg-zinc-800 p-4 rounded-lg hover:bg-zinc-700 transition cursor-pointer">
                        <div className="flex items-start justify-between mb-2">
                          <div>
                            <h4 className="text-white font-bold">Padrão</h4>
                            <p className="text-2xl font-bold text-white mt-1">R$ 39,90<span className="text-sm text-gray-400">/mês</span></p>
                          </div>
                          <span className="px-2 py-1 bg-yellow-600 text-black text-xs font-bold rounded">POPULAR</span>
                        </div>
                        <ul className="space-y-1 text-sm text-gray-300 mt-3">
                          <li>✓ Qualidade Full HD (1080p)</li>
                          <li>✓ 2 telas simultâneas</li>
                          <li>✓ Catálogo completo</li>
                          <li>✓ Download em 2 dispositivos</li>
                        </ul>
                      </div>

                      {/* Premium Plan - Current */}
                      <div className="bg-zinc-800 p-4 rounded-lg border-2 border-netflix-primary opacity-60">
                        <div className="flex items-start justify-between mb-2">
                          <div>
                            <h4 className="text-white font-bold">Premium</h4>
                            <p className="text-2xl font-bold text-white mt-1">R$ 55,90<span className="text-sm text-gray-400">/mês</span></p>
                          </div>
                          <span className="px-2 py-1 bg-netflix-primary text-black text-xs font-bold rounded">SEU PLANO</span>
                        </div>
                        <ul className="space-y-1 text-sm text-gray-300 mt-3">
                          <li>✓ Qualidade 4K + HDR</li>
                          <li>✓ 4 telas simultâneas</li>
                          <li>✓ Catálogo completo</li>
                          <li>✓ Download em 4 dispositivos</li>
                          <li>✓ Áudio espacial</li>
                        </ul>
                      </div>
                    </div>
                  </div>

                  {/* Payment Method */}
                  <div className="bg-zinc-800 p-5 rounded-lg">
                    <h3 className="text-lg font-bold text-white mb-3">Forma de Pagamento</h3>
                    <div className="flex items-center gap-3">
                      <div className="w-12 h-8 bg-gradient-to-r from-blue-600 to-blue-400 rounded flex items-center justify-center text-white text-xs font-bold">
                        VISA
                      </div>
                      <div>
                        <p className="text-white font-semibold">•••• •••• •••• 4242</p>
                        <p className="text-sm text-gray-400">Válido até 12/2027</p>
                      </div>
                    </div>
                    <button className="w-full mt-4 px-4 py-2 bg-zinc-700 text-white font-semibold rounded hover:bg-zinc-600 transition">
                      Alterar Forma de Pagamento
                    </button>
                  </div>

                  <button className="w-full px-4 py-2 text-netflix-primary font-semibold hover:underline">
                    Cancelar Assinatura
                  </button>
                </div>
              )}
            </div>
          </div>
        </div>
      );
    }
    if (modal.type === "details" && selected) {
      const isFilme = selected.tipo === "Filme";
      const isSerie = selected.tipo === "Serie";
      return (
        <div className="fixed inset-0 bg-black/80 flex items-center justify-center z-[999] p-4">
          <div className="bg-zinc-900 rounded-lg w-full max-w-2xl relative max-h-[90vh] overflow-y-auto">
            <button
              className="absolute top-4 right-4 text-white text-3xl hover:text-gray-300 z-10"
              onClick={() => setModal(null)}
            >
              &times;
            </button>
            
            {/* Header */}
            <div className="p-6 pb-4">
              <h2 className="text-3xl font-bold text-white mb-3">
                {selected.titulo}
              </h2>
              
              <div className="flex items-center gap-3 mb-4 flex-wrap">
                <span className="px-3 py-1 bg-green-600 text-white font-bold rounded text-sm">
                  {selected.classificacao === 0
                    ? "Livre"
                    : `+${selected.classificacao}`}
                </span>
                <span className="text-gray-300">{selected.tipo}</span>
                <span className="text-gray-400">•</span>
                <span className="text-gray-300">{selected.genero}</span>
              </div>
            </div>

            {/* Details */}
            <div className="px-6 pb-6">
              <div className="bg-zinc-800 p-4 rounded-lg mb-4">
                <h3 className="text-lg font-bold text-white mb-3">Informações</h3>
                <div className="space-y-2 text-sm">
                  {isFilme && (
                    <>
                      <div className="flex gap-8">
                        <span className="text-gray-400 min-w-[240px]">Duração:</span>
                        <span className="text-white">{(selected as Filme).duracaoMinutos || "-"} minutos</span>
                      </div>
                      <div className="flex gap-8">
                        <span className="text-gray-400 min-w-[240px]">Gênero:</span>
                        <span className="text-white">{selected.genero}</span>
                      </div>
                      <div className="flex gap-8">
                        <span className="text-gray-400 min-w-[240px]">Tipo:</span>
                        <span className="text-white">Filme</span>
                      </div>
                    </>
                  )}
                  {isSerie && (
                    <>
                      <div className="flex gap-8">
                        <span className="text-gray-400 min-w-[240px]">Temporadas:</span>
                        <span className="text-white">{(selected as Serie).temporadas || "-"}</span>
                      </div>
                      <div className="flex gap-8">
                        <span className="text-gray-400 min-w-[240px]">Episódios por temporada:</span>
                        <span className="text-white">{(selected as Serie).episodiosPorTemporada || "8"}</span>
                      </div>
                      <div className="flex gap-8">
                        <span className="text-gray-400 min-w-[240px]">Duração do episódio:</span>
                        <span className="text-white">60 minutos</span>
                      </div>
                      <div className="flex gap-8">
                        <span className="text-gray-400 min-w-[240px]">Gênero:</span>
                        <span className="text-white">{selected.genero}</span>
                      </div>
                      <div className="flex gap-8">
                        <span className="text-gray-400 min-w-[240px]">Tipo:</span>
                        <span className="text-white">Série</span>
                      </div>
                    </>
                  )}
                  <div className="flex gap-8">
                    <span className="text-gray-400 min-w-[240px]">Classificação:</span>
                    <span className="text-white">
                      {selected.classificacao === 0
                        ? "Livre para todos os públicos"
                        : `Não recomendado para menores de ${selected.classificacao} anos`}
                    </span>
                  </div>
                </div>
              </div>

              <button
                className="w-full px-6 py-3 bg-netflix-primary text-black font-bold rounded hover:bg-yellow-400 transition transform hover:scale-105"
                onClick={() => {
                  navigate(`/watch/${encodeURIComponent(selected.titulo)}`);
                  setModal(null);
                }}
              >
                Assistir Agora
              </button>
            </div>
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
      <div className="mt-8 relative z-10">
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
        <div id="minha-lista">
          {catalogo.length > 0 && (
            <Row
              title="Minha Lista"
              items={catalogo}
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
