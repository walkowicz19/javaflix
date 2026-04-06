import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Plus, Pencil } from 'lucide-react';

interface Profile {
  id: string;
  name: string;
  avatar: string;
  isKids: boolean;
}

const defaultAvatars = [
  'https://i.pravatar.cc/150?img=1',
  'https://i.pravatar.cc/150?img=2',
  'https://i.pravatar.cc/150?img=3',
  'https://i.pravatar.cc/150?img=4',
  'https://i.pravatar.cc/150?img=5',
  'https://i.pravatar.cc/150?img=6',
  'https://i.pravatar.cc/150?img=7',
  'https://i.pravatar.cc/150?img=8',
];

export const Profiles = () => {
  const navigate = useNavigate();
  const [profiles, setProfiles] = useState<Profile[]>([
    { id: '1', name: 'Matheus', avatar: defaultAvatars[0], isKids: false },
    { id: '2', name: 'Maria', avatar: defaultAvatars[1], isKids: false },
    { id: '3', name: 'João', avatar: defaultAvatars[2], isKids: false },
    { id: '4', name: 'Kids', avatar: defaultAvatars[3], isKids: true },
  ]);
  const [isManaging, setIsManaging] = useState(false);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [newProfileName, setNewProfileName] = useState('');
  const [selectedAvatar, setSelectedAvatar] = useState(defaultAvatars[0]);
  const [isKidsProfile, setIsKidsProfile] = useState(false);

  const handleSelectProfile = (profile: Profile) => {
    if (isManaging) return;
    // Save selected profile to localStorage
    localStorage.setItem('selectedProfile', JSON.stringify(profile));
    navigate('/browse');
  };

  const handleCreateProfile = () => {
    if (!newProfileName.trim()) return;
    
    const newProfile: Profile = {
      id: Date.now().toString(),
      name: newProfileName,
      avatar: selectedAvatar,
      isKids: isKidsProfile,
    };
    
    setProfiles([...profiles, newProfile]);
    setShowCreateModal(false);
    setNewProfileName('');
    setSelectedAvatar(defaultAvatars[0]);
    setIsKidsProfile(false);
  };

  const handleDeleteProfile = (id: string) => {
    setProfiles(profiles.filter(p => p.id !== id));
  };

  return (
    <div className="min-h-screen bg-netflix-black flex flex-col items-center justify-center px-4">
      {/* Logo */}
      <div className="absolute top-8 left-8">
        <h1 className="text-netflix-primary text-4xl font-bold">JAVAFLIX</h1>
      </div>

      <div className="w-full max-w-6xl">
        <h1 className="text-5xl md:text-6xl text-white font-medium text-center mb-12">
          Quem está assistindo?
        </h1>

        {/* Profiles Grid */}
        <div className="flex flex-wrap justify-center gap-8 mb-12">
          {profiles.map((profile) => (
            <div
              key={profile.id}
              className="group relative"
              onClick={() => handleSelectProfile(profile)}
            >
              <div className="relative cursor-pointer">
                <div className="w-32 h-32 md:w-40 md:h-40 rounded-lg overflow-hidden border-4 border-transparent group-hover:border-white transition-all duration-300">
                  <img
                    src={profile.avatar}
                    alt={profile.name}
                    className="w-full h-full object-cover"
                  />
                  {isManaging && (
                    <div className="absolute inset-0 bg-black/70 flex items-center justify-center">
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          handleDeleteProfile(profile.id);
                        }}
                        className="p-2 bg-red-600 rounded-full hover:bg-red-700 transition"
                      >
                        <Pencil className="w-6 h-6 text-white" />
                      </button>
                    </div>
                  )}
                </div>
                <p className="text-gray-400 group-hover:text-white text-center mt-3 text-lg font-medium transition">
                  {profile.name}
                </p>
                {profile.isKids && (
                  <span className="absolute top-0 right-0 bg-yellow-500 text-black text-xs font-bold px-2 py-1 rounded">
                    KIDS
                  </span>
                )}
              </div>
            </div>
          ))}

          {/* Add Profile Button */}
          {profiles.length < 5 && (
            <div
              className="group cursor-pointer"
              onClick={() => setShowCreateModal(true)}
            >
              <div className="w-32 h-32 md:w-40 md:h-40 rounded-lg border-4 border-transparent group-hover:border-white transition-all duration-300 flex items-center justify-center bg-zinc-800">
                <Plus className="w-16 h-16 text-gray-400 group-hover:text-white transition" />
              </div>
              <p className="text-gray-400 group-hover:text-white text-center mt-3 text-lg font-medium transition">
                Adicionar Perfil
              </p>
            </div>
          )}
        </div>

        {/* Manage Profiles Button */}
        <div className="text-center">
          <button
            onClick={() => setIsManaging(!isManaging)}
            className="px-8 py-3 text-gray-400 hover:text-white border border-gray-400 hover:border-white text-lg font-medium transition"
          >
            {isManaging ? 'Concluído' : 'Gerenciar Perfis'}
          </button>
        </div>
      </div>

      {/* Create Profile Modal */}
      {showCreateModal && (
        <div className="fixed inset-0 bg-black/80 flex items-center justify-center z-50 px-4" onClick={() => setShowCreateModal(false)}>
          <div className="bg-zinc-900 rounded-lg w-full max-w-2xl p-8" onClick={(e) => e.stopPropagation()}>
            <h2 className="text-3xl text-white font-medium mb-6">Adicionar Perfil</h2>
            
            <div className="space-y-6">
              {/* Avatar Selection */}
              <div>
                <label className="block text-white text-sm font-semibold mb-3">Escolha um avatar:</label>
                <div className="grid grid-cols-4 md:grid-cols-8 gap-3">
                  {defaultAvatars.map((avatar, index) => (
                    <div
                      key={index}
                      className={`cursor-pointer rounded-lg overflow-hidden border-4 transition ${
                        selectedAvatar === avatar ? 'border-white' : 'border-transparent hover:border-gray-500'
                      }`}
                      onClick={() => setSelectedAvatar(avatar)}
                    >
                      <img src={avatar} alt={`Avatar ${index + 1}`} className="w-full h-full object-cover" />
                    </div>
                  ))}
                </div>
              </div>

              {/* Name Input */}
              <div>
                <label className="block text-white text-sm font-semibold mb-2">Nome:</label>
                <input
                  type="text"
                  value={newProfileName}
                  onChange={(e) => setNewProfileName(e.target.value)}
                  placeholder="Digite o nome do perfil"
                  className="w-full p-3 bg-zinc-800 text-white rounded border border-zinc-700 focus:border-white outline-none"
                  maxLength={20}
                  autoFocus
                />
              </div>

              {/* Kids Profile Toggle */}
              <div className="flex items-center gap-3">
                <input
                  type="checkbox"
                  id="kidsProfile"
                  checked={isKidsProfile}
                  onChange={(e) => setIsKidsProfile(e.target.checked)}
                  className="w-5 h-5 accent-netflix-primary"
                />
                <label htmlFor="kidsProfile" className="text-white font-medium cursor-pointer">
                  Perfil infantil (apenas conteúdo adequado para crianças)
                </label>
              </div>

              {/* Buttons */}
              <div className="flex gap-4 pt-4">
                <button
                  onClick={handleCreateProfile}
                  disabled={!newProfileName.trim()}
                  className="flex-1 px-6 py-3 bg-white text-black font-bold rounded hover:bg-netflix-primary transition disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  Criar Perfil
                </button>
                <button
                  onClick={() => setShowCreateModal(false)}
                  className="flex-1 px-6 py-3 bg-zinc-700 text-white font-bold rounded hover:bg-zinc-600 transition"
                >
                  Cancelar
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

// Made with Bob