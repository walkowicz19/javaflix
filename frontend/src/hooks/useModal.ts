import { useState } from 'react';
import type { Conteudo } from '../types';

export type ModalType = 'search' | 'notifications' | 'user' | 'details' | null;

export interface ModalState {
  type: ModalType;
}

/**
 * Controla qual modal está aberto e qual item de conteúdo está selecionado.
 * Extrai o estado de UI modal de App.tsx para reduzir sua responsabilidade.
 */
export function useModal() {
  const [modal, setModal] = useState<ModalState | null>(null);
  const [selected, setSelected] = useState<Conteudo | null>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [userTab, setUserTab] = useState<'preferences' | 'plan'>('preferences');

  const openModal = (type: ModalType, item?: Conteudo) => {
    if (item) setSelected(item);
    setModal(type ? { type } : null);
  };

  const closeModal = () => {
    setModal(null);
    setSearchQuery('');
  };

  const handleCardClick = (item: Conteudo) => {
    setSelected(item);
    setModal({ type: 'details' });
  };

  return {
    modal,
    selected,
    searchQuery,
    setSearchQuery,
    userTab,
    setUserTab,
    openModal,
    closeModal,
    handleCardClick,
  };
}
