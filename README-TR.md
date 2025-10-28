# Llama 3 ve Ollama Kurulumu

## Gereksinimler
- En az 8GB RAM
- Linux
- curl

## Kurulum

### 1. Otomatik Kurulum
`setup-llama.sh` scripti:
- Sistem gereksinimlerini kontrol eder (RAM, OS, curl)
- Ollama'yı otomatik kurar
- Llama 3 modelini indirir
- Servisleri test eder

```bash
chmod +x setup-llama.sh
./setup-llama.sh
```

### 2. Manuel Kurulum

#### Ollama Kurulumu
```bash
# Linux (Resmi installer)
curl -fsSL https://ollama.ai/install.sh | sh
```

#### Llama 3 Modeli İndirme
```bash
ollama pull llama3
```

## Çalıştırma

### Ollama Servisini Başlat
`start-llama.sh` scripti:
- Ollama'nın kurulu olup olmadığını kontrol eder
- Ollama servisini başlatır
- Llama 3 modelinin mevcut olup olmadığını kontrol eder
- Modeli indirir (gerekirse)
- Servisi çalışır durumda tutar

```bash
chmod +x start-llama.sh
./start-llama.sh
```

### Manuel Başlatma
```bash
ollama serve
ollama run llama3
```

## Test
```bash
# API testi
curl http://localhost:11434/api/tags

# Model testi
ollama run llama3 "Merhaba"
```

## Port
- Ollama: `http://localhost:11434`
- Spring Boot: `http://localhost:5004`

## Sorun Giderme
```bash
# Ollama durumunu kontrol et
ps aux | grep ollama

# Logları kontrol et
tail -f /tmp/ollama.log

# Ollama'yı yeniden başlat
pkill ollama && ollama serve
```
