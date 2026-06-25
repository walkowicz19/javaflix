import { useState, useEffect } from 'react';
import { getCatalogo } from '../services/api';
import type { Conteudo } from '../types';

interface CatalogoState {
  catalogo: Conteudo[];
  loading: boolean;
}

/**
 * Busca o catálogo do backend uma única vez na montagem do componente.
 * Isola o efeito de fetch do componente App — mantém App.tsx focado em renderização.
 */
export function useCatalogo(): CatalogoState {
  const [catalogo, setCatalogo] = useState<Conteudo[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    getCatalogo().then((data) => {
      if (!cancelled) {
        setCatalogo(data);
        setLoading(false);
      }
    });
    return () => { cancelled = true; };
  }, []);

  return { catalogo, loading };
}
