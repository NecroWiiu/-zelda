# -*- coding: utf-8 -*-
"""Sintetiza los sonidos (ocarina, UI, esquive...) y los codifica a .ogg con ffmpeg."""
import math, os, random, struct, subprocess, wave, shutil

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
OUT = os.path.join(ROOT, "src/main/resources/assets/hyrulecraft/sounds")
SR = 32000

def env(t, dur, a=0.01, r=0.15):
    if t < a: return t / a
    if t > dur - r: return max(0.0, (dur - t) / r)
    return 1.0

def tone(freq, dur, vib=5.0, vib_depth=0.006, harm=(1.0, 0.35, 0.12), vol=0.5, decay=0.0):
    n = int(SR * dur); out = []
    ph = 0.0
    for i in range(n):
        t = i / SR
        f = freq * (1 + vib_depth * math.sin(2 * math.pi * vib * t) * min(1, t * 6))
        ph += 2 * math.pi * f / SR
        s = sum(h * math.sin(ph * (k + 1)) for k, h in enumerate(harm))
        out.append(s * vol * env(t, dur) * math.exp(-decay * t))
    return out

def mix(*parts):
    n = max(len(p) for p in parts)
    return [sum(p[i] for p in parts if i < len(p)) for i in range(n)]

def seq(notes, gap=0.0):
    out = []
    for s in notes: out += s + [0.0] * int(SR * gap)
    return out

def sweep(f0, f1, dur, vol=0.4, noise=0.0):
    n = int(SR * dur); out = []; ph = 0.0; r = random.Random(1)
    for i in range(n):
        t = i / SR; f = f0 + (f1 - f0) * t / dur
        ph += 2 * math.pi * f / SR
        out.append((math.sin(ph) * (1 - noise) + r.uniform(-1, 1) * noise) * vol * env(t, dur, 0.01, dur * 0.6))
    return out

def write(name, samples):
    os.makedirs(OUT, exist_ok=True)
    wav = os.path.join("/tmp", name + ".wav")
    peak = max(1e-6, max(abs(s) for s in samples))
    k = min(1.0, 0.9 / peak)
    with wave.open(wav, "wb") as f:
        f.setnchannels(1); f.setsampwidth(2); f.setframerate(SR)
        f.writeframes(b"".join(struct.pack("<h", int(max(-1, min(1, s * k)) * 32767)) for s in samples))
    ogg = os.path.join(OUT, name + ".ogg")
    subprocess.run(["ffmpeg", "-y", "-loglevel", "error", "-i", wav, "-c:a", "libvorbis", "-q:a", "3", ogg], check=True)

NOTE = {"a": 293.66, "down": 349.23, "right": 440.00, "left": 493.88, "up": 587.33}

def run():
    if not shutil.which("ffmpeg"):
        print("  (ffmpeg no encontrado: sonidos omitidos)"); return
    for k, f in NOTE.items():
        write(f"ocarina_{k}", tone(f, 0.7, vol=0.5, decay=1.2))
    write("song_ok", seq([tone(f, 0.16, vol=0.4) for f in (293.66, 349.23, 440.0, 587.33)] + [tone(587.33, 0.5, vol=0.45, decay=2)]))
    write("z_target", mix(sweep(600, 900, 0.12, 0.4), sweep(900, 1200, 0.12, 0.2)))
    write("navi_ping", seq([tone(1800, 0.09, vib=0, harm=(1, 0.2), vol=0.4), tone(2300, 0.14, vib=0, harm=(1, 0.2), vol=0.4)], 0.03))
    write("roll", sweep(500, 120, 0.28, 0.5, noise=0.8))
    write("rupee", seq([tone(1568, 0.1, vib=0, harm=(1, 0.5, 0.2), vol=0.4, decay=8), tone(2093, 0.22, vib=0, harm=(1, 0.5, 0.2), vol=0.4, decay=6)]))
    write("secret", seq([tone(f, 0.13, vib=0, vol=0.4) for f in (392, 349, 311, 220, 207, 293, 330, 415)] ))
    write("hookshot", mix(sweep(1200, 300, 0.18, 0.4, noise=0.4), tone(180, 0.1, vib=0, harm=(1,), vol=0.5, decay=20)))
    write("spin_charge", sweep(300, 1400, 0.6, 0.35))
    print("  Sonidos .ogg generados")

if __name__ == "__main__":
    run()
