import axios from 'axios';
import type { Conteudo } from '../types';

// Lido da variável de ambiente VITE_API_URL definida em .env.local (dev)
// ou injetada no build de produção. Fallback apenas para facilitar
// primeiras execuções sem arquivo .env configurado.
const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8083/api';

export const api = axios.create({
    baseURL: API_URL,
});

export const getCatalogo = async (): Promise<Conteudo[]> => {
    try {
        const response = await api.get('/catalogo');
        return response.data;
    } catch (error) {
        console.error("Erro ao buscar catálogo:", error);
        return [];
    }
};

export const buscarConteudo = async (query: string): Promise<Conteudo | null> => {
    try {
        // Sanitize and validate input
        const sanitized = query.trim();
        if (!sanitized || sanitized.length > 100) {
            throw new Error("Invalid search query");
        }
        
        // Use encodeURIComponent to prevent injection
        const response = await api.get(`/buscar?q=${encodeURIComponent(sanitized)}`);
        return response.data;
    } catch (error) {
        console.error("Erro ao buscar conteúdo:", error);
        return null;
    }
};
