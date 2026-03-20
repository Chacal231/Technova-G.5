/**
 * Escena 3D RTX para el hero (NeoLux)
 */
export async function initRtx3D() {
  const canvas = document.getElementById('rtxCanvas');
  if (!canvas) return;

  const gl = canvas.getContext?.('webgl2') || canvas.getContext?.('webgl');
  if (!gl) return;

  let THREE;
  let OrbitControls;
  try {
    THREE = await import('https://unpkg.com/three@0.160.0/build/three.module.js');
    OrbitControls = (await import('https://unpkg.com/three@0.160.0/examples/jsm/controls/OrbitControls.js')).OrbitControls;
  } catch (_) {
    return;
  }

  const reducedMotion = window.matchMedia?.('(prefers-reduced-motion: reduce)')?.matches;
  const lowPower = (navigator.hardwareConcurrency && navigator.hardwareConcurrency <= 4)
    || (navigator.deviceMemory && navigator.deviceMemory <= 4);

  const scene = new THREE.Scene();
  const camera = new THREE.PerspectiveCamera(42, 1, 0.1, 100);
  camera.position.set(0, 0.3, 9.4);

  const renderer = new THREE.WebGLRenderer({
    canvas,
    alpha: true,
    antialias: !lowPower,
    powerPreference: 'high-performance'
  });
  const pixelRatioCap = lowPower ? 1 : 1.6;
  renderer.setPixelRatio(Math.min(window.devicePixelRatio || 1, pixelRatioCap));

  const controls = new OrbitControls(camera, renderer.domElement);
  controls.enablePan = false;
  controls.enableZoom = false;
  controls.enableDamping = !reducedMotion;
  controls.dampingFactor = 0.08;
  controls.minPolarAngle = Math.PI / 5;
  controls.maxPolarAngle = Math.PI - Math.PI / 5;

  // Luces
  scene.add(new THREE.AmbientLight(0xffffff, 0.35));
  const key = new THREE.DirectionalLight(0x6ee7ff, 0.95);
  key.position.set(8, 8, 7);
  scene.add(key);
  const rim = new THREE.PointLight(0x8b5cf6, 1.2, 70);
  rim.position.set(-6, 2, -3);
  scene.add(rim);

  // GPU estilizada
  const gpu = new THREE.Group();
  scene.add(gpu);

  const bodyMat = new THREE.MeshPhysicalMaterial({
    color: 0x0f172a,
    metalness: 0.9,
    roughness: 0.28,
    clearcoat: 0.7,
    clearcoatRoughness: 0.18,
    emissive: 0x0ea5e9,
    emissiveIntensity: 0.18,
    transparent: true,
    opacity: 0.88
  });
  const topMat = new THREE.MeshPhysicalMaterial({
    color: 0x111827,
    metalness: 0.95,
    roughness: 0.2,
    clearcoat: 0.9,
    clearcoatRoughness: 0.12,
    emissive: 0x8b5cf6,
    emissiveIntensity: 0.12,
    transparent: true,
    opacity: 0.85
  });
  const lineMat = new THREE.MeshBasicMaterial({
    color: 0x6ee7ff,
    transparent: true,
    opacity: 0.25
  });

  const body = new THREE.Mesh(new THREE.BoxGeometry(5.2, 1.0, 2.35), bodyMat);
  body.position.y = -0.05;
  gpu.add(body);

  const top = new THREE.Mesh(new THREE.BoxGeometry(5.2, 0.2, 1.7), topMat);
  top.position.set(0, 0.38, 0.2);
  gpu.add(top);

  const addFan = (xPos) => {
    const fanGroup = new THREE.Group();
    fanGroup.position.set(xPos, 0.4, 0.18);

    const rimGeo = new THREE.TorusGeometry(0.64, 0.08, lowPower ? 14 : 22, lowPower ? 36 : 56);
    const rimMesh = new THREE.Mesh(rimGeo, bodyMat);
    rimMesh.rotation.x = Math.PI / 2;
    fanGroup.add(rimMesh);

    const hubGeo = new THREE.CylinderGeometry(0.17, 0.17, 0.16, lowPower ? 16 : 28);
    const hubMesh = new THREE.Mesh(hubGeo, topMat);
    hubMesh.rotation.x = Math.PI / 2;
    fanGroup.add(hubMesh);

    const blades = new THREE.Group();
    const bladesCount = lowPower ? 6 : 9;
    for (let i = 0; i < bladesCount; i++) {
      const blade = new THREE.Mesh(new THREE.BoxGeometry(0.09, 0.48, 0.2), bodyMat);
      const angle = (i / bladesCount) * Math.PI * 2;
      blade.position.set(Math.cos(angle) * 0.36, Math.sin(angle) * 0.36, 0);
      blade.rotation.z = angle + 0.5;
      blades.add(blade);
    }
    fanGroup.add(blades);
    fanGroup.userData.blades = blades;
    gpu.add(fanGroup);
  };

  addFan(-1.55);
  addFan(1.55);

  const glow = new THREE.Mesh(new THREE.RingGeometry(2.5, 2.85, lowPower ? 36 : 56), lineMat);
  glow.rotation.x = Math.PI / 2;
  glow.position.set(0, -0.3, 0.1);
  gpu.add(glow);

  const resize = () => {
    const rect = canvas.getBoundingClientRect();
    const w = Math.max(200, rect.width);
    const h = Math.max(200, rect.height);
    renderer.setSize(w, h, false);
    camera.aspect = w / h;
    camera.updateProjectionMatrix();
  };
  resize();
  const ro = new ResizeObserver(() => resize());
  ro.observe(canvas);

  const wrap = canvas.closest('.hero-rtx-render');
  if (wrap) {
    wrap.classList.add('three-ready');
    wrap.querySelectorAll('.hero-rtx-model, .hero-rtx-grid, .hero-rtx-scan').forEach(el => {
      el.style.opacity = '0';
      el.style.pointerEvents = 'none';
    });
  }

  let interacting = false;
  controls.addEventListener('start', () => { interacting = true; });
  controls.addEventListener('end', () => { interacting = false; });

  const mats = [];
  gpu.traverse((obj) => {
    if (obj.material) {
      if (Array.isArray(obj.material)) mats.push(...obj.material);
      else mats.push(obj.material);
    }
  });

  let last = performance.now();
  let opacity = 0.88;
  let rafId = 0;
  let running = true;
  const animate = (t) => {
    if (!running) return;
    const dt = Math.min(0.033, (t - last) / 1000);
    last = t;

    gpu.traverse((obj) => {
      if (obj.userData.blades) obj.userData.blades.rotation.z += dt * 2.2;
    });
    if (!interacting && !reducedMotion) gpu.rotation.y += dt * 0.015;

    const target = interacting ? 0.6 : 0.88;
    opacity += (target - opacity) * 0.09;
    mats.forEach((m) => {
      if ('opacity' in m) m.opacity = opacity;
    });

    controls.update();
    renderer.render(scene, camera);
    rafId = requestAnimationFrame(animate);
  };
  const onVisibility = () => {
    if (document.hidden) {
      running = false;
      if (rafId) cancelAnimationFrame(rafId);
      rafId = 0;
      return;
    }
    if (!running) {
      running = true;
      last = performance.now();
      rafId = requestAnimationFrame(animate);
    }
  };
  document.addEventListener('visibilitychange', onVisibility);
  rafId = requestAnimationFrame(animate);
}
